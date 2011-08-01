package org.tynamo.security.federatedaccounts.twitter;

import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;

public class TwitterAuthenticationToken extends OauthAccessToken {

	private static final long serialVersionUID = 0L;

	public TwitterAuthenticationToken(String accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}

}