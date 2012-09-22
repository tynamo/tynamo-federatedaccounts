package org.tynamo.security.federatedaccounts.github.services;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

public class GitHubApi extends DefaultApi20 {

	public String getAccessTokenEndpoint() {
		return "https://github.com/login/oauth/access_token";
	}

	public String getAuthorizationUrl(OAuthConfig config) {
		return "https://github.com/login/oauth/authorize?client_id=" + config.getApiKey();
	}

}
