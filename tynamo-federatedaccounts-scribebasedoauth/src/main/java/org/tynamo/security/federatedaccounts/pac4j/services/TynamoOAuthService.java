package org.tynamo.security.federatedaccounts.pac4j.services;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public interface TynamoOAuthService extends OAuthService {

	String getApiName();

	AuthenticationToken getAuthenticationToken(Token accessToken);

	Object callback();

}
