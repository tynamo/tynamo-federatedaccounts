package org.tynamo.security.federatedaccounts.pac4j.services;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.pac4j.oauth.client.BaseOAuthClient;
import org.pac4j.oauth.client.DropBoxClient;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.YahooClient;

public class Pac4jOauthClientLocatorImpl implements Pac4jOauthClientLocator {

	private String dropboxClientId;
	private String dropboxClientSecret;
	private String facebookClientId;
	private String facebookClientSecret;
	private String yahooClientId;
	private String yahooClientSecret;

	public Pac4jOauthClientLocatorImpl(@Symbol(Pac4jFederatedRealm.DROPBOX_CLIENTID) String dropboxClientId,
		@Symbol(Pac4jFederatedRealm.DROPBOX_CLIENTSECRET) String dropboxClientSecret,
		@Symbol(Pac4jFederatedRealm.FACEBOOK_CLIENTID) String facebookClientId,
		@Symbol(Pac4jFederatedRealm.FACEBOOK_CLIENTSECRET) String facebookClientSecret,
		@Symbol(Pac4jFederatedRealm.YAHOO_CLIENTID) String yahooClientId,
		@Symbol(Pac4jFederatedRealm.YAHOO_CLIENTSECRET) String yahooClientSecret) {
		this.dropboxClientId = dropboxClientId;
		this.dropboxClientSecret = dropboxClientSecret;
		this.facebookClientId = facebookClientId;
		this.facebookClientSecret = facebookClientSecret;
		this.yahooClientId = yahooClientId;
		this.yahooClientSecret = yahooClientSecret;
	}

	@Override
	public BaseOAuthClient getClient(String clientName) {
		SupportedClient client = SupportedClient.valueOf(clientName);
		switch (client) {
		case dropbox:
			return new DropBoxClient(dropboxClientId, dropboxClientSecret);
		case facebook:
			return new FacebookClient(facebookClientId, facebookClientSecret);
		case yahoo:
			return new YahooClient(yahooClientId, yahooClientSecret);
			// case github:
			// case google2:
			// case linkedin2:
			// case twitter:
			// case windowslive:
			// case wordpress:
			// case yahoo:
		}
		return null;
	}
}
