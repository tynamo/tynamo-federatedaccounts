package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tynamo.security.federatedaccounts.base.FacebookOauthComponentBase;
import org.tynamo.security.federatedaccounts.facebook.FacebookRealm;
import org.tynamo.security.federatedaccounts.util.WindowMode;

@Import(library = "FacebookSignIn.js", stylesheet = "fb-button.css")
public class FacebookSignIn extends FacebookOauthComponentBase {
	@Inject
	@Symbol(FacebookRealm.FACEBOOK_PERMISSIONS)
	@Property
	private String facebookPermissions;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private WindowMode windowMode;

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	public String getOauthAuthorizationLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://graph.facebook.com/oauth/authorize?client_id=");
		sb.append(getOauthClientId());
		sb.append("&redirect_uri=");
		sb.append(getOauthRedirectLink(windowMode));
		sb.append("&scope=");
		sb.append(facebookPermissions);

		return sb.toString();
	}
}
