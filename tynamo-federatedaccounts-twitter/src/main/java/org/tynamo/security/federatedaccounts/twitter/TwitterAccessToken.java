package org.tynamo.security.federatedaccounts.twitter;

import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;

import twitter4j.auth.AccessToken;

public class TwitterAccessToken extends OauthAccessToken {

	private static final long serialVersionUID = 0L;

	public TwitterAccessToken(AccessToken accessToken) {
		super(accessToken, null);
	}

	@Override
	public Object getPrincipal() {
		return ((AccessToken) super.getCredentials()).getUserId();
	}
}