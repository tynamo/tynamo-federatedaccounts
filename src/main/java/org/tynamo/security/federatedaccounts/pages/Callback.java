package org.tynamo.security.federatedaccounts.pages;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.services.OAuthServicetLocator;
import org.tynamo.security.federatedaccounts.services.TynamoOAuthService;

public class Callback {

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Inject
	private OAuthServicetLocator locator;

	@SessionState(create = false)
	private Token requestToken;

	private boolean requestTokenExists;

	Object onActivate(String api) {

		TynamoOAuthService service = locator.getService(api);

		String code = null;

		if ("1.0".equals(service.getVersion())) {

			code = request.getParameter("oauth_verifier");

			if (!requestTokenExists) {
				logger.error("No RequestToken SSO (Session State Object)");
				return null;
			}


		} else if ("2.0".equals(service.getVersion())) {

			code = request.getParameter("code");

		} else {
			logger.error("Unsupported OAuth version");
		}

		if (code == null) {
			logger.error("No OAuth authentication code provided");
			return null;
		}

		Verifier verifier = new Verifier(code);
		Token accessToken = service.getAccessToken(requestToken, verifier);

		try {
			SecurityUtils.getSubject().login(service.getAuthenticationToken(accessToken));

			logger.info("User successfully authenticated");
		} catch (AuthenticationException e) {
			logger.error("Using access token " + accessToken + "\nCould not sign in federated user because of: ", e);
		}

		return service.callback();

	}
}

