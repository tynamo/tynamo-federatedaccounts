package org.tynamo.security.federatedaccounts.pac4j.services;

public interface OAuthServiceLocator {

	TynamoOAuthService getService(String api);
}
