package org.tynamo.security.federatedaccounts.twitter.pages;

import java.net.MalformedURLException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.twitter.base.AbstractTwitterOauthPage;

import twitter4j.TwitterException;

public class CommitTwitterOauth extends AbstractTwitterOauthPage {

	@CommitAfter
	@Override
	protected Object onActivate(EventContext context) throws TwitterException, MalformedURLException {
		return super.onActivate(context);
	}
}
