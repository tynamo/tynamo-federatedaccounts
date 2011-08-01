package org.tynamo.security.federatedaccounts.twitter.pages;

import java.net.MalformedURLException;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.twitter.base.AbstractTwitterOauthPage;

public class CommitTwitterOauth extends AbstractTwitterOauthPage {

	@CommitAfter
	@Override
	protected void onActivate(String windowMode) throws MalformedURLException {
		super.onActivate(windowMode);
	}
}
