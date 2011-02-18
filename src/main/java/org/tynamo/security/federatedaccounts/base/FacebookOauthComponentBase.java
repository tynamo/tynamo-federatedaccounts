package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.facebook.FacebookRealm;
import org.tynamo.security.federatedaccounts.pages.CommitFacebookOauth;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;

public class FacebookOauthComponentBase {
	@Inject
	@Symbol(FederatedAccountSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	public boolean getAutocommit() {
		return autocommit;
	}

	@Inject
	@Symbol(FacebookRealm.FACEBOOK_CLIENTID)
	private String oauthClientId;

	public String getOauthClientId() {
		return oauthClientId;
	}

	@Inject
	@Symbol(FacebookRealm.FACEBOOK_CLIENTSECRET)
	private String oauthClientSecret;

	protected String getOauthClientSecret() {
		return oauthClientSecret;
	}

	public boolean isOauthConfigured() {
		return !"".equals(oauthClientId) && !"".equals(oauthClientSecret);
	}

	@Inject
	private PageRenderLinkSource linkSource;

	public String getOauthRedirectLink() {
		// return "http://" + hostName + linkSource.createPageRenderLink(autocommit ? CommitFacebookOauth.class :
		// FacebookOauth.class).toURI();
		return linkSource.createPageRenderLink(autocommit ? CommitFacebookOauth.class : FacebookOauth.class).toAbsoluteURI();
	}
}
