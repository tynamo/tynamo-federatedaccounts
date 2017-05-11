package org.tynamo.security.federatedaccounts.pac4j.pages;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oauth.client.DropBoxClient;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.base.AbstractOauthPage;
import org.tynamo.security.federatedaccounts.pac4j.Pac4jAuthenticationToken;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jOauthClientLocator;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jOauthClient;

public class Pac4jOauth extends AbstractOauthPage {
	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Inject
	private HttpServletRequest httpRequest;

	@Inject
	private HttpServletResponse httpResponse;

	@Inject
	private AlertManager alertManager;

	// private String returnUri;

	@Inject
	private Cookies cookies;

	@Inject
	private Pac4jOauthClientLocator oauthClientLocator;

	private String clientName;

	@Override
	protected Object onOauthActivate(EventContext eventContext) throws Exception {
		J2EContext context = new J2EContext(httpRequest, httpResponse);
		clientName = eventContext.get(String.class, 1);
		Pac4jOauthClient client = oauthClientLocator.getClient(clientName);
		client.setReadTimeout(20000);
		client.setConnectTimeout(20000);	
		if (eventContext.getCount() > 3) {
			String action = eventContext.get(String.class, 2);
			// pass along this redirectUrl
			setReturnUri(eventContext.get(String.class, 3));
			client.setCallbackUrl(getOauthRedirectLink(getWindowMode(), clientName));
			if ("request_token".equals(action)) {
				String providerOauthUrl = client.getRedirectionUrl(context);
				// fix an issue with pac4j dropboxClient and "direct redirection"
				// TODO: Check, maybe already fixed in latest versions of pac4j?
				if (client.instance() instanceof DropBoxClient)
					if (!providerOauthUrl.contains("&oauth_callback"))
						providerOauthUrl += "&oauth_callback=" + client.getCallbackUrl();
				// Because Google requires the callback uri to match *exactly* (including query parameters) with the configuration,
				// use a cookie to store the destination uri after successful Oauth authentication
				cookies.writeCookieValue("oauth_returnpage", getReturnUri(), -1);
				return new URL(providerOauthUrl);
			}
		}
		// FIXME should we catch and handle the event context errors? Probably

		// capture the redirect url again
		String returnPageUri = cookies.readCookieValue("oauth_returnpage");
		if (returnPageUri != null) {
			setReturnUri(returnPageUri);
			cookies.removeCookieValue("oauth_returnpage");
		}

		// we'll get a technical exception if callback url is not set even though it shouldn't be needed anymore
		client.setCallbackUrl(getOauthRedirectLink(getWindowMode(), clientName));

		CommonProfile userProfile = client.getUserProfile(context);
		Pac4jAuthenticationToken accessToken = new Pac4jAuthenticationToken(userProfile);

		// TODO just use the default rememberMe for now. We could later add support for providing rememberMe in context
		accessToken.setRememberMe(isRememberMe());

		try {
			SecurityUtils.getSubject().login(accessToken);
			alertManager.success("User successfully authenticated");
			setOauthAuthenticated(true);
		} catch (AuthenticationException e) {
			logger.error(
				"Using access token " + accessToken
					+ String.format("\nCould not sign in a %s federated user because of: ", client.getName(), e));
			// FIXME Deal with other account exception types like expired and
			// locked
			alertManager.error(String.format("A %s federated user cannot be signed in, report this to support.\n %s",
				client.getName(), e.getMessage()));
		}
		client = null;
		return null;
	}

	public String getProviderPrefix() {
		return FederatedAccountType.pac4j_ + clientName;
	}

}
