package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentClassResolver;

@Import(library = "OauthSignIn.js")
public abstract class AbstractOauthSignIn extends OauthComponentBase {
	@Inject
	private ComponentClassResolver resolver;

	// Use ^ for current page the component is contained in, empty for configured success url
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "literal:")
	private String returnPageName;

	public String getReturnPageName() {
		return returnPageName;
	}
}
