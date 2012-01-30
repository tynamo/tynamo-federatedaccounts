package org.tynamo.security.federatedaccounts.twitter.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.twitter.pages.CommitTwitterOauth;
import org.tynamo.security.federatedaccounts.twitter.pages.TwitterOauth;
import org.tynamo.security.federatedaccounts.twitter.services.TwitterRealm;
import org.tynamo.security.federatedaccounts.util.WindowMode;

import twitter4j.TwitterFactory;

public class TwitterOauthComponentBase {
	@Inject
	@Symbol(FederatedAccountSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	public boolean getAutocommit() {
		return autocommit;
	}

	@Inject
	@Symbol(TwitterRealm.TWITTER_CLIENTID)
	private String oauthClientId;

	public String getOauthClientId() {
		return oauthClientId;
	}

	@Inject
	@Symbol(TwitterRealm.TWITTER_CLIENTSECRET)
	private String oauthClientSecret;

	protected String getOauthClientSecret() {
		return oauthClientSecret;
	}

	public boolean isOauthConfigured() {
		return !"".equals(oauthClientId) && !"".equals(oauthClientSecret);
	}

	@Inject
	private PageRenderLinkSource linkSource;

	@Inject
	private TwitterFactory twitterFactory;

	protected TwitterFactory getTwitterFactory() {
		return twitterFactory;
	}

	// Final since signin and oauth *must* share the same implementation (or at least use the same link)
	protected final String getOauthRedirectLink(Object... context) {
		if (context == null || !(context[0] instanceof WindowMode))
			throw new IllegalArgumentException("WindowMode is required as the first context parameter");
		return linkSource.createPageRenderLinkWithContext(autocommit ? CommitTwitterOauth.class : TwitterOauth.class,
			context).toAbsoluteURI();
	}
}
