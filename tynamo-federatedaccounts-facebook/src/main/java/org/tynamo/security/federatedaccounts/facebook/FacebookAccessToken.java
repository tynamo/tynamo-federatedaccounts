package org.tynamo.security.federatedaccounts.facebook;

import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;

public class FacebookAccessToken extends OauthAccessToken {

	private static final long serialVersionUID = 0L;

	public FacebookAccessToken(String accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}

}