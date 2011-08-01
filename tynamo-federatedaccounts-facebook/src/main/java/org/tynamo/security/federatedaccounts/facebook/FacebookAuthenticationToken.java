package org.tynamo.security.federatedaccounts.facebook;

import org.scribe.model.Token;
import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAuthenticationToken;

public class FacebookAuthenticationToken extends OauthAuthenticationToken {

	private static final long serialVersionUID = 0L;

	public FacebookAuthenticationToken(Token accessToken, long expiresInSeconds) {
		super(accessToken, expiresInSeconds);
	}

}