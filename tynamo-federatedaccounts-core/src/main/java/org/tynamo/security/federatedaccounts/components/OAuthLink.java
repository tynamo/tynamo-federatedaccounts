package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.scribe.model.Token;
import org.tynamo.security.federatedaccounts.services.OAuthServicetLocator;
import org.tynamo.security.federatedaccounts.services.TynamoOAuthService;

/**
 * Generates a link to the service provider authorization endpoint.
 *
 */
public class OAuthLink {

	private static final Token EMPTY_TOKEN = null;

	@Inject
	private ComponentResources resources;

	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String provider;

	@Inject
	private OAuthServicetLocator locator;

	@SessionState(create = false)
	private Token requestToken;

	void beginRender(MarkupWriter writer) {
		writer.element("a", "href", getAuthorizationUrl());
		resources.renderInformalParameters(writer);
	}

	void afterRender(MarkupWriter writer) {
		writer.end(); // <a>
	}

	private String getAuthorizationUrl() {

		TynamoOAuthService service = locator.getService(provider);

		if ("1.0".equals(service.getVersion())) {
			requestToken = service.getRequestToken();
			return service.getAuthorizationUrl(requestToken);
		} else {
			return service.getAuthorizationUrl(EMPTY_TOKEN);
		}
	}


}
