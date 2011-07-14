package org.tynamo.security.federatedaccounts.facebook.pages;

import java.net.MalformedURLException;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.facebook.base.AbstractFacebookOauthPage;

public class CommitFacebookOauth extends AbstractFacebookOauthPage {

	@CommitAfter
	@Override
	protected void onActivate(String windowMode) throws MalformedURLException {
		super.onActivate(windowMode);
	}
}
