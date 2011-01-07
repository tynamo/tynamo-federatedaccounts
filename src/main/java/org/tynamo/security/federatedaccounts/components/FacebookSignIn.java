package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.HostSymbols;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;

@Import(library = "FacebookSignIn.js", stylesheet = "fb-button.css")
public class FacebookSignIn {

	@SuppressWarnings("unused")
	@Inject
	@Symbol(FacebookOauth.FACEBOOK_CLIENTID)
	@Property
	private String facebookClientId;

	@SuppressWarnings("unused")
	@Inject
	@Symbol(FacebookOauth.FACEBOOK_PERMISSIONS)
	@Property
	private String facebookPermissions;

	@Inject
	@Symbol(HostSymbols.BASEURI)
	private String baseUri;

	@Inject
	private PageRenderLinkSource linkSource;

	public String getOauthRedirectLink() {
		return baseUri + linkSource.createPageRenderLink(FacebookOauth.class).toAbsoluteURI();
	}
}
