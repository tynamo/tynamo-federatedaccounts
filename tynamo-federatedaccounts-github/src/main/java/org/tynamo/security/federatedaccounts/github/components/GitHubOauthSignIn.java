package org.tynamo.security.federatedaccounts.github.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.tynamo.security.federatedaccounts.base.AbstractOauthSignIn;
import org.tynamo.security.federatedaccounts.github.pages.GitHubOauth;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public class GitHubOauthSignIn extends AbstractOauthSignIn {

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "800")
	@Property
	private Integer width;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "400")
	@Property
	private Integer height;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private WindowMode windowMode;

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	protected Class<?> getOauthPageClass() {
		return GitHubOauth.class;
	}

	public String getOauthAuthorizationLink() {
		return getOauthRedirectLink(windowMode, "request_token");
	}

}
