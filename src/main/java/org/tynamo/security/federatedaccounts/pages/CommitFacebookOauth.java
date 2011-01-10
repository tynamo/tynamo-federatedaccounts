package org.tynamo.security.federatedaccounts.pages;

import java.net.MalformedURLException;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.base.AbstractFacebookOauth;

public class CommitFacebookOauth extends AbstractFacebookOauth {

	@CommitAfter
	@Override
	protected void onActivate() throws MalformedURLException {
		super.onActivate();
	}
}
