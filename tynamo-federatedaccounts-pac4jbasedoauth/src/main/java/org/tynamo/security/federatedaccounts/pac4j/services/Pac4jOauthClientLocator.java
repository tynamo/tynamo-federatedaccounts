package org.tynamo.security.federatedaccounts.pac4j.services;

public interface Pac4jOauthClientLocator {
	enum SupportedClient {
		facebook, dropbox, github, google2, linkedin2, twitter, windowslive, wordpress, yahoo
	}

	public Pac4jOauthClient getClient(String clientName);

}
