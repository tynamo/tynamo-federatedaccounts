package org.tynamo.security.federatedaccounts.twitter.pages;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.twitter.base.AbstractTwitterOauthPage;

import twitter4j.TwitterException;

public class CommitTwitterOauth extends AbstractTwitterOauthPage {

	@CommitAfter
	@Override
	protected void onActivate(String windowMode) throws TwitterException {
		super.onActivate(windowMode);
	}
}
