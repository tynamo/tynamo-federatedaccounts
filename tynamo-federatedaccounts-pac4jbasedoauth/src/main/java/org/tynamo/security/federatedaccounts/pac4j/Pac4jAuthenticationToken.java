package org.tynamo.security.federatedaccounts.pac4j;

import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.OAuth20Profile;

public class Pac4jAuthenticationToken implements RememberMeAuthenticationToken {

	private static final long serialVersionUID = 0L;

	private UserProfile profile;
	private String accessToken;

	private boolean rememberMe;

	public Pac4jAuthenticationToken(UserProfile profile) {
		this.profile = profile;
		// if (profile instanceof OAuth10Profile) this.accessToken = ((OAuth10Profile) profile).getAccessSecret();
		if (profile instanceof OAuth20Profile) this.accessToken = ((OAuth20Profile) profile).getAccessToken();
	}

	@Override
	public Object getCredentials() {
		return accessToken;
	}

	@Override
	public Object getPrincipal() {
		return profile;
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;

	}
}
