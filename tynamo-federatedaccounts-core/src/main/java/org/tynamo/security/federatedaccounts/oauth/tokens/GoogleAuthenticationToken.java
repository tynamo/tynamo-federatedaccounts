package org.tynamo.security.federatedaccounts.oauth.tokens;


public class GoogleAuthenticationToken extends OauthAccessToken {

	private static final long serialVersionUID = 0L;

//	public GoogleAuthenticationToken(Token accessToken, long expiresInSeconds) {
	public GoogleAuthenticationToken(String accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}
}
