package org.tynamo.security.federatedaccounts.services;

public interface OAuthServicetLocator {

	TynamoOAuthService getService(String api);
}
