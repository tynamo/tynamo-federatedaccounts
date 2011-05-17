package org.tynamo.security.federatedaccounts.pages;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.annotations.Property;
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

	@Property
	private String print;

	Object onActivate(String api) {

		TynamoOAuthService service = locator.getService(api);

		String code = request.getParameter("code");
		if (code == null) {
			logger.error("No Oauth authentication code provided");
			return null;
		}

		Verifier verifier = new Verifier(code);
		Token accessToken = service.getAccessToken(null, verifier);

		try {
			SecurityUtils.getSubject().login(service.getAuthenticationToken(accessToken));

			logger.info("User successfully authenticated");
		} catch (AuthenticationException e) {
			logger.error("Using access token " + accessToken + "\nCould not sign in federated user because of: ", e);
		}

		return service.callback();

	}
}

