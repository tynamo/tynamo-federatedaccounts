package org.tynamo.security.federatedaccounts.openid.base;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.shiro.SecurityUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.URLEncoder;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Identifier;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.base.AbstractOauthSignIn;
import org.tynamo.security.federatedaccounts.openid.OpenidAccessToken;
import org.tynamo.security.federatedaccounts.openid.services.OpenidLoginManager;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public abstract class AbstractOpenidAuthPage extends AbstractOauthSignIn {
	@Inject
	@Symbol(FederatedAccountSymbols.HTTPCLIENT_ON_GAE)
	private boolean httpClientOnGae;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Inject
	private PageRenderLinkSource linkSource;

	@Property
	private WindowMode windowMode;

	@Inject
	private OpenidLoginManager loginManager;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Response response;

	@Inject
	private URLEncoder urlEncoder;

	protected URL onActivate(String redirect) throws MalformedURLException {
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
