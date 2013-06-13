package org.tynamo.security.federatedaccounts.pac4j.services;

import org.apache.tapestry5.ioc.ObjectLocator;

public class OAuthServiceLocatorImpl implements OAuthServiceLocator {

	private ObjectLocator locator;

	public OAuthServiceLocatorImpl(ObjectLocator locator) {
		this.locator = locator;
	}


	@Override
	public TynamoOAuthService getService(String api) {
		return locator.getService(api, TynamoOAuthService.class);
	}
}
