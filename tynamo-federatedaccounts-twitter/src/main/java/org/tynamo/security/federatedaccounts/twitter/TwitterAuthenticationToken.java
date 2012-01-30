package org.tynamo.security.federatedaccounts.twitter;

import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;

import twitter4j.auth.AccessToken;

public class TwitterAuthenticationToken extends OauthAccessToken {

	private static final long serialVersionUID = 0L;

	private AccessToken accessToken;

	public TwitterAuthenticationToken(AccessToken accessToken, long expiresInSeconds) {
		super(accessToken.getToken(), expiresInSeconds);
		this.accessToken = accessToken;
	}

	@Override
	public Object getPrincipal() {
		return accessToken;
	}

	// credentials cannot be null
	@Override
	public Object getCredentials() {
		return accessToken.getUserId();
	}

}