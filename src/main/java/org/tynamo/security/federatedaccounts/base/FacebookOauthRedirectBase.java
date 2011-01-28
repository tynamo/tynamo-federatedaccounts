package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.HostSymbols;
import org.tynamo.security.federatedaccounts.pages.CommitFacebookOauth;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;

public class FacebookOauthRedirectBase {
	@Inject
	@Symbol(HostSymbols.HOSTNAME)
	private String hostName;

	@Inject
	@Symbol(HostSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	@Inject
	@Symbol(FacebookOauth.FACEBOOK_CLIENTID)
	private String facebookClientId;

	public String getFacebookClientId() {
		return facebookClientId;
	}

	@Inject
	@Symbol(FacebookOauth.FACEBOOK_CLIENTSECRET)
	private String facebookClientSecret;

	public String getFacebookClientSecret() {
		return facebookClientSecret;
	}

	public boolean isConfigured() {
		return !"".equals(facebookClientId) && !"".equals(facebookClientSecret);
	}

	@Inject
	private PageRenderLinkSource linkSource;

	public String getOauthRedirectLink() {
		return "http://" + hostName + linkSource.createPageRenderLink(autocommit ? CommitFacebookOauth.class : FacebookOauth.class).toURI();
	}

	public String getSuccessLink() {
		return "http://" + hostName;
	}
}
