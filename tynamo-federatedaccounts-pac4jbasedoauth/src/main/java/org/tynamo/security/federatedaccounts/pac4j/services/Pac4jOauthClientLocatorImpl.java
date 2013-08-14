package org.tynamo.security.federatedaccounts.pac4j.services;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.pac4j.oauth.client.BaseOAuthClient;
import org.pac4j.oauth.client.DropBoxClient;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.LinkedIn2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oauth.client.WindowsLiveClient;
import org.pac4j.oauth.client.WordPressClient;
import org.pac4j.oauth.client.YahooClient;

public class Pac4jOauthClientLocatorImpl implements Pac4jOauthClientLocator {

	private String dropboxClientId;
	private String dropboxClientSecret;
	private String facebookClientId;
	private String facebookClientSecret;
	private String yahooClientId;
	private String yahooClientSecret;
	private String githubClientId;
	private String githubClientSecret;
	private String googleClientId;
	private String googleClientSecret;
	private String linkedinClientId;
	private String linkedinClientSecret;
	private String twitterClientId;
	private String twitterClientSecret;
	private String windowsliveClientId;
	private String windowsliveClientSecret;
	private String wordpressClientId;
	private String wordpressClientSecret;

	public Pac4jOauthClientLocatorImpl(@Symbol(Pac4jFederatedRealm.DROPBOX_CLIENTID) String dropboxClientId,
		@Symbol(Pac4jFederatedRealm.DROPBOX_CLIENTSECRET) String dropboxClientSecret,
		@Symbol(Pac4jFederatedRealm.FACEBOOK_CLIENTID) String facebookClientId,
		@Symbol(Pac4jFederatedRealm.FACEBOOK_CLIENTSECRET) String facebookClientSecret,
		@Symbol(Pac4jFederatedRealm.GITHUB_CLIENTID) String githubClientId,
		@Symbol(Pac4jFederatedRealm.GITHUB_CLIENTSECRET) String githubClientSecret,
		@Symbol(Pac4jFederatedRealm.GOOGLE_CLIENTID) String googleClientId,
		@Symbol(Pac4jFederatedRealm.GOOGLE_CLIENTSECRET) String googleClientSecret,
		@Symbol(Pac4jFederatedRealm.LINKEDIN_CLIENTID) String linkedinClientId,
		@Symbol(Pac4jFederatedRealm.LINKEDIN_CLIENTSECRET) String linkedinClientSecret,
		@Symbol(Pac4jFederatedRealm.TWITTER_CLIENTID) String twitterClientId,
		@Symbol(Pac4jFederatedRealm.TWITTER_CLIENTSECRET) String twitterClientSecret,
		@Symbol(Pac4jFederatedRealm.WINDOWSLIVE_CLIENTID) String windowsliveClientId,
		@Symbol(Pac4jFederatedRealm.WINDOWSLIVE_CLIENTSECRET) String windowsliveClientSecret,
		@Symbol(Pac4jFederatedRealm.WORDPRESS_CLIENTID) String wordpressClientId,
		@Symbol(Pac4jFederatedRealm.WORDPRESS_CLIENTSECRET) String wordpressClientSecret,
		@Symbol(Pac4jFederatedRealm.YAHOO_CLIENTID) String yahooClientId,
		@Symbol(Pac4jFederatedRealm.YAHOO_CLIENTSECRET) String yahooClientSecret) {
		this.dropboxClientId = dropboxClientId;
		this.dropboxClientSecret = dropboxClientSecret;
		this.facebookClientId = facebookClientId;
		this.facebookClientSecret = facebookClientSecret;
		this.githubClientId = githubClientId;
		this.githubClientSecret = githubClientSecret;
		this.googleClientId = googleClientId;
		this.googleClientSecret = googleClientSecret;
		this.linkedinClientId = linkedinClientId;
		this.linkedinClientSecret = linkedinClientSecret;
		this.twitterClientId = twitterClientId;
		this.twitterClientSecret = twitterClientSecret;
		this.windowsliveClientId = windowsliveClientId;
		this.windowsliveClientSecret = windowsliveClientSecret;
		this.wordpressClientId = wordpressClientId;
		this.wordpressClientSecret = wordpressClientSecret;
		this.yahooClientId = yahooClientId;
		this.yahooClientSecret = yahooClientSecret;
	}

	@Override
	public BaseOAuthClient<?> getClient(String clientName) {
		SupportedClient client = SupportedClient.valueOf(clientName);
		switch (client) {
		case dropbox:
			return new DropBoxClient(dropboxClientId, dropboxClientSecret);
		case facebook:
			return new FacebookClient(facebookClientId, facebookClientSecret);
		case github:
			return new GitHubClient(githubClientId, githubClientSecret);
		case google2:
			return new Google2Client(googleClientId, googleClientSecret);
		case linkedin2:
			return new LinkedIn2Client(linkedinClientId, linkedinClientSecret);
		case twitter:
			return new TwitterClient(twitterClientId, twitterClientSecret);
		case windowslive:
			return new WindowsLiveClient(windowsliveClientId, windowsliveClientSecret);
		case wordpress:
			return new WordPressClient(wordpressClientId, wordpressClientSecret);
		case yahoo:
			return new YahooClient(yahooClientId, yahooClientSecret);
		default:
			throw new IllegalArgumentException("Client " + clientName + " is not one of the supported clients "
				+ SupportedClient.values());
		}
	}
}
