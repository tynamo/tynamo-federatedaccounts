package org.tynamo.security.federatedaccounts.oauth.tokens;

import org.scribe.model.Token;

public class FacebookAuthenticationToken extends OauthAuthenticationToken {

	private static final long serialVersionUID = 0L;

	public FacebookAuthenticationToken(Token accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}

}