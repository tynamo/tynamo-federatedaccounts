package org.tynamo.security.federatedaccounts.github.pages;

import java.net.URL;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.base.AbstractOauthPage;
import org.tynamo.security.federatedaccounts.components.FlashMessager;
import org.tynamo.security.federatedaccounts.github.services.GitHubAccessToken;
import org.tynamo.security.federatedaccounts.github.services.GitHubApi;
import org.tynamo.security.federatedaccounts.github.services.GitHubRealm;

@SuppressWarnings("deprecation")
public class GitHubOauth extends AbstractOauthPage {

	@Inject
	@Symbol(FederatedAccountSymbols.SUCCESSURL)
	private String successUrl;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Component
	private FlashMessager flashMessager;

	@Inject
	private PageRenderLinkSource linkSource;

	private boolean oauthAuthenticated;

	@Inject
	@Symbol(GitHubRealm.GITHUB_SCOPE)
	private String githubScope;

	protected Object onOauthActivate(EventContext eventContext) throws Exception {
		ServiceBuilder serviceBuilder = new ServiceBuilder().provider(GitHubApi.class).apiKey(getOauthClientId())
			.apiSecret(getOauthClientSecret()).callback(getSuccessLink());

		OAuthService oAuthService = serviceBuilder.build();

		if (eventContext.getCount() > 1) {
			String action = eventContext.get(String.class, 1);
			if ("request_token".equals(action)) { return new URL(oAuthService.getAuthorizationUrl(null)
				+ (githubScope != null ? "&scope=" + githubScope : "") + "&redirect_uri="
				+ getOauthRedirectLink(getWindowMode())); }
		}

		String code = request.getParameter("code");
		if (code == null) {
			flashMessager.setFailureMessage("No Oauth verifier code provided");
			return null;
		}

		Token accessToken = oAuthService.getAccessToken(new Token(code, getOauthClientSecret()), new Verifier(code));

		try {
			SecurityUtils.getSubject().login(new GitHubAccessToken(accessToken));

			logger.info("principal = {}", SecurityUtils.getSubject().getPrincipal());

			flashMessager.setSuccessMessage("User successfully authenticated");
			oauthAuthenticated = true;
		} catch (AuthenticationException e) {
			logger.error("Using access token " + accessToken + "\nCould not sign in a GitHub federated user because of: ", e);
			// FIXME Deal with other account exception types like expired and
			// locked
			flashMessager.setFailureMessage("A GitHub federated user cannot be signed in, report this to support.\n "
				+ e.getMessage());
		}
		return null;
	}

	@Inject
	private BaseURLSource baseURLSource;

	public String getSuccessLink() {
		return "".equals(successUrl) ? "" : baseURLSource.getBaseURL(request.isSecure()) + successUrl;
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (oauthAuthenticated)
			javaScriptSupport.addScript("onAuthenticationSuccess('" + getSuccessLink() + "', '" + getWindowMode().name()
				+ "');");
	}
}
