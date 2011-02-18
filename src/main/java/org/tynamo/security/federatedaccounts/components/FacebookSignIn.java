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
		// return "https://graph.facebook.com/oauth/authorize?client_id&#61;" + getOauthClientId() +
		// "&amp;redirect_uri&#61;"
		// + getOauthRedirectLink() + "&amp;scope&#61;" + facebookPermissions;
		StringBuilder sb = new StringBuilder();
		sb.append("https://graph.facebook.com/oauth/authorize?client_id&#61;");
		sb.append(getOauthClientId());
		sb.append("&amp;redirect_uri&#61;");
		sb.append(getOauthRedirectLink());
		sb.append("&amp;scope&#61;");
		sb.append(facebookPermissions);

		// return sb.toString();
		return getOauthRedirectLink();
	}

	@Override
	public String getOauthRedirectLink() {
		return super.getOauthRedirectLink() + '/' + windowMode;
	}

}
