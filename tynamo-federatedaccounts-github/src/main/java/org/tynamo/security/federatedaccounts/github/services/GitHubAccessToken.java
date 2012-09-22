package org.tynamo.security.federatedaccounts.github.services;

import org.scribe.model.Token;
import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;

public class GitHubAccessToken extends OauthAccessToken {

	private static final long serialVersionUID = 1L;

	public GitHubAccessToken(Token accessToken) {
		super(accessToken.getToken(), null);
	}

}
