package org.tynamo.security.federatedaccounts.pac4j.services;

import org.pac4j.oauth.client.BaseOAuthClient;

public interface Pac4jOauthClientLocator {
	enum SupportedClient {
		facebook, dropbox, github, google2, linkedin2, twitter, windowslive, wordpress, yahoo
	}

	public BaseOAuthClient getClient(String clientName);

}
