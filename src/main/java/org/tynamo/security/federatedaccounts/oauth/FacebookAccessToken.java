package org.tynamo.security.federatedaccounts.oauth;

import java.util.Date;

public class FacebookAccessToken extends OauthAccessToken {
	private static final long serialVersionUID = 0L;

	public FacebookAccessToken(String accessToken, long expiresInSeconds) {
		// http://developers.facebook.com/docs/authentication/ specifies expires as
		// "number of seconds until the token expires"
		super(accessToken, new Date(System.currentTimeMillis() + expiresInSeconds * 1000L));
	}

	public FacebookAccessToken(String accessToken, Date expiration) {
		super(accessToken, expiration);
	}

}
