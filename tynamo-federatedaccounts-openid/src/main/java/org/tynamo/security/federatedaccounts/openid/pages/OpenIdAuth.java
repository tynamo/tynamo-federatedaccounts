package org.tynamo.security.federatedaccounts.openid.pages;

import java.net.URL;

import org.apache.shiro.SecurityUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.URLEncoder;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Identifier;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.base.AbstractOauthPage;
import org.tynamo.security.federatedaccounts.components.FlashMessager;
import org.tynamo.security.federatedaccounts.openid.OpenidAccessToken;
import org.tynamo.security.federatedaccounts.openid.services.OpenidLoginManager;

public class OpenIdAuth extends AbstractOauthPage {
	@Inject
	@Symbol(FederatedAccountSymbols.HTTPCLIENT_ON_GAE)
	private boolean httpClientOnGae;

	@Inject
	@Symbol(FederatedAccountSymbols.SUCCESSURL)
	private String successUrl;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Inject
	private PageRenderLinkSource linkSource;

	private boolean fbAuthenticated;

	@Inject
	private OpenidLoginManager loginManager;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Response response;

	@Component
	private FlashMessager flashMessager;

	@Inject
	private URLEncoder urlEncoder;

	@Inject
	private BaseURLSource baseURLSource;

	public String getSuccessLink() {
		return "".equals(successUrl) ? "" : baseURLSource.getBaseURL(request.isSecure()) + successUrl;
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (fbAuthenticated)
			javaScriptSupport.addScript("onAuthenticationSuccess('" + getSuccessLink() + "', '" + getWindowMode().name()
				+ "');");
	}

	@Override
	protected Object onOauthActivate(EventContext context) throws Exception {
		if (context.getCount() < 1) {
			flashMessager.setFailureMessage("No openID redirect link provided");
			return null;
		}
		String redirect = context.get(String.class, 0);

		try {
			String returningPage = urlEncoder.decode(redirect);

			VerificationResult verification = loginManager.authenticate(requestGlobals.getHTTPServletRequest());
			Identifier verified = verification.getVerifiedId();
			if (verified != null) {
				logger.info("Authenticated: " + verified.getIdentifier());
				SecurityUtils.getSubject().login(new OpenidAccessToken(verified.getIdentifier(), verification));
				logger.info(" * succesfully logged in");

				logger.info("Returning to: " + returningPage);
				// response.sendRedirect(returningPage);
				return new URL(returningPage);
			} else {
				logger.error("Authentication failed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
