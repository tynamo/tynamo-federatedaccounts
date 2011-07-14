package org.tynamo.security.federatedaccounts.services;

public interface OAuthServiceLocator {

	TynamoOAuthService getService(String api);
}
