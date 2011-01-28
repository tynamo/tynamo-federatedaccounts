package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tynamo.security.federatedaccounts.base.FacebookOauthRedirectBase;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;

@Import(library = "FacebookSignIn.js", stylesheet = "fb-button.css")
public class FacebookSignIn extends FacebookOauthRedirectBase {
	@SuppressWarnings("unused")
	@Inject
	@Symbol(FacebookOauth.FACEBOOK_PERMISSIONS)
	@Property
	private String facebookPermissions;

}
