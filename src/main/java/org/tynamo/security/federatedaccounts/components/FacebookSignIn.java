package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tynamo.security.federatedaccounts.base.FacebookOauthComponentBase;
import org.tynamo.security.federatedaccounts.facebook.FacebookRealm;

@Import(library = "FacebookSignIn.js", stylesheet = "fb-button.css")
public class FacebookSignIn extends FacebookOauthComponentBase {
	@SuppressWarnings("unused")
	@Inject
	@Symbol(FacebookRealm.FACEBOOK_PERMISSIONS)
	@Property
	private String facebookPermissions;

}
