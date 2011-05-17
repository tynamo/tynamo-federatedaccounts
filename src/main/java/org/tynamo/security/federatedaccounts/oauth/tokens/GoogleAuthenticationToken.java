package org.tynamo.security.federatedaccounts.oauth.tokens;

import org.scribe.model.Token;

public class GoogleAuthenticationToken extends OauthAuthenticationToken {

	private static final long serialVersionUID = 0L;

	public GoogleAuthenticationToken(Token accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}

}
