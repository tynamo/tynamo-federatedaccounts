package org.tynamo.security.federatedaccounts.pages;

import java.net.MalformedURLException;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.base.AbstractFacebookOauthPage;

public class CommitFacebookOauth extends AbstractFacebookOauthPage {

	@CommitAfter
	@Override
	protected void onActivate() throws MalformedURLException {
		super.onActivate();
	}
}
