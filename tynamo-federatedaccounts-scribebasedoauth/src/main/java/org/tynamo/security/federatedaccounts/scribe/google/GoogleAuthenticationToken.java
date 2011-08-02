package org.tynamo.security.federatedaccounts.scribe.google;

import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;


public class GoogleAuthenticationToken extends OauthAccessToken {

	private static final long serialVersionUID = 0L;

//	public GoogleAuthenticationToken(Token accessToken, long expiresInSeconds) {
	public GoogleAuthenticationToken(String accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}
}
