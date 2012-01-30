package org.tynamo.security.federatedaccounts.twitter.pages;

import java.net.MalformedURLException;

import org.apache.tapestry5.EventContext;
import org.tynamo.security.federatedaccounts.twitter.base.AbstractTwitterOauthPage;

import twitter4j.TwitterException;

public class TwitterOauth extends AbstractTwitterOauthPage {

	@Override
	protected Object onActivate(EventContext context) throws TwitterException, MalformedURLException {
		return super.onActivate(context);
	}
}
