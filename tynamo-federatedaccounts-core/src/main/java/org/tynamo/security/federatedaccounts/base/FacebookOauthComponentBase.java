package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.realms.FacebookRealm;
import org.tynamo.security.federatedaccounts.pages.CommitFacebookOauth;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;
import org.tynamo.security.federatedaccounts.util.WindowMode;

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

	// Final since signin and oauth *must* share the same implementation (or at least use the same link)
	protected final String getOauthRedirectLink(WindowMode windowMode) {
		return linkSource.createPageRenderLinkWithContext(autocommit ? CommitFacebookOauth.class : FacebookOauth.class, windowMode)
				.toAbsoluteURI();
	}
}
