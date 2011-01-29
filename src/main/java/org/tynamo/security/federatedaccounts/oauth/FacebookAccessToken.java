package org.tynamo.security.federatedaccounts.oauth;

import java.util.Date;

public class FacebookAccessToken extends OauthAccessToken {
	private static final long serialVersionUID = 0L;

	public FacebookAccessToken(String accessToken, long expiration) {
		super(accessToken, new Date(expiration));
	}

	public FacebookAccessToken(String accessToken, Date expiration) {
		super(accessToken, expiration);
	}

}
