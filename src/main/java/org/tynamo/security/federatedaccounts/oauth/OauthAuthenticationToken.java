package org.tynamo.security.federatedaccounts.oauth;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.model.Token;

import java.util.Date;

public class OauthAuthenticationToken implements AuthenticationToken {

	private static final long serialVersionUID = 0L;
	private Token token;
	private Date expiration;

	public OauthAuthenticationToken(Token accessToken, long expiresInSeconds) {
		this(accessToken, new Date(System.currentTimeMillis() + expiresInSeconds * 1000L));
	}

	public OauthAuthenticationToken(Token accessToken, Date expiration) {
		this.token = accessToken;
		this.expiration = expiration;
	}

	public Token getToken() {
		return token;
	}

	public Date getExpiration() {
		return expiration;
	}

	public String toString() {
		return token.getToken();
	}

	/**
	 * @return the granted oauth access token
	 */
	@Override
	public Object getPrincipal() {
		return getToken();
	}

	@Override
	public Object getCredentials() {
		return getExpiration();
	}

}
