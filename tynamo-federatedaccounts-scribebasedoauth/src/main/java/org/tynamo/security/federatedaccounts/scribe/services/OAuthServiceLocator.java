package org.tynamo.security.federatedaccounts.scribe.services;

public interface OAuthServiceLocator {

	TynamoOAuthService getService(String api);
}
