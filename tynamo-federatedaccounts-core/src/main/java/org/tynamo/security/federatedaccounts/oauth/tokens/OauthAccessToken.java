package org.tynamo.security.federatedaccounts.oauth.tokens;

import java.io.Serializable;
import java.util.Date;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

public class OauthAccessToken implements RememberMeAuthenticationToken {

	private static final long serialVersionUID = 1L;
	private Serializable token;
	private Date expiration;
	private boolean rememberMe;

	public OauthAccessToken(Serializable accessToken, long expiresInSeconds) {
		this(accessToken, expiresInSeconds < 0 ? null : new Date(System.currentTimeMillis() + expiresInSeconds * 1000L));
	}

	public OauthAccessToken(Serializable accessToken, Date expiration) {
		this.token = accessToken;
		this.expiration = expiration;
	}

	public Date getExpiration() {
		return expiration;
	}

	public String toString() {
		return token.toString();
	}

	/**
	 * @return the granted oauth access token
	 */
	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
