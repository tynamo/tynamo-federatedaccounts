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
	private PageRenderLinkSource linkSource;

	public String getOauthRedirectLink() {
		return "http://" + hostName + linkSource.createPageRenderLink(autocommit ? CommitFacebookOauth.class : FacebookOauth.class).toURI();
	}

}
