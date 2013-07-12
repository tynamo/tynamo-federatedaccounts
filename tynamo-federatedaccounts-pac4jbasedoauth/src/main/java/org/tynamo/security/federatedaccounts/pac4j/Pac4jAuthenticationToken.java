package org.tynamo.security.federatedaccounts.pac4j;

import org.apache.shiro.authc.AuthenticationToken;
import org.pac4j.core.profile.UserProfile;

public class Pac4jAuthenticationToken implements AuthenticationToken {

	private static final long serialVersionUID = 0L;

	private UserProfile profile;
	private String accessToken;

	// public GoogleAuthenticationToken(Token accessToken, long expiresInSeconds) {
	public Pac4jAuthenticationToken(UserProfile profile, String accessToken) {
		// FIXME perhaps we should accept the whole org.pac4j.oauth.credentials.OAuthCredentials
		// and hold onto it, instead of just the String token from the credentials. See Pac4jOauth
		this.profile = profile;
		this.accessToken = accessToken;
	}

	@Override
	public Object getCredentials() {
		return accessToken;
	}

	@Override
	public Object getPrincipal() {
		return profile;
	}
}
