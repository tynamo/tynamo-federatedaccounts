package org.tynamo.security.federatedaccounts.oauth;

import java.util.Date;

import org.apache.shiro.authc.AuthenticationToken;

public class OauthAccessToken implements AuthenticationToken {
	private static final long serialVersionUID = 0L;
	private String token;

	private Date expiration;

	public OauthAccessToken(String token, long expiration) {
		this(token, new Date(expiration));
	}

	public OauthAccessToken(String accessToken, Date expiration) {
		this.token = accessToken;
		this.expiration = expiration;
	}

	public String getToken() {
		return token;
	}

	public Date getExpiration() {
		return expiration;
	}

	public String toString() {
		return getToken();
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
