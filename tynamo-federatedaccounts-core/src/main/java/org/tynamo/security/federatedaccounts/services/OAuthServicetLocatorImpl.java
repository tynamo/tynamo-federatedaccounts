package org.tynamo.security.federatedaccounts.services;

import org.apache.tapestry5.ioc.ObjectLocator;

public class OAuthServicetLocatorImpl implements OAuthServicetLocator {

	private ObjectLocator locator;

	public OAuthServicetLocatorImpl(ObjectLocator locator) {
		this.locator = locator;
	}


	@Override
	public TynamoOAuthService getService(String api) {
		return locator.getService(api, TynamoOAuthService.class);
	}
}
