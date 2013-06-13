package org.tynamo.security.federatedaccounts.pac4j.services;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.tynamo.security.federatedaccounts.pac4j.AuthenticationTokenBuilder;

public class TynamoOAuthServiceImpl implements TynamoOAuthService {

	private String apiName;
	private OAuthService wrappedService;
	private Class callbackPageClass;
	private AuthenticationTokenBuilder authenticationTokenBuilder;

	public TynamoOAuthServiceImpl(String apiName, OAuthService wrappedService, Class callbackPageClass, AuthenticationTokenBuilder authenticationTokenBuilder) {
		this.apiName = apiName;
		this.wrappedService = wrappedService;
		this.callbackPageClass = callbackPageClass;
		this.authenticationTokenBuilder = authenticationTokenBuilder;
	}

	@Override
	public String getApiName() {
		return apiName;
	}

	@Override
	public AuthenticationToken getAuthenticationToken(Token accessToken) {
		return authenticationTokenBuilder.getAuthenticationToken(accessToken);
	}

	@Override
	public Object callback() {
		return callbackPageClass;
	}

	@Override
	public Token getRequestToken() {
		return wrappedService.getRequestToken();
	}

	@Override
	public Token getAccessToken(Token requestToken, Verifier verifier) {
		return wrappedService.getAccessToken(requestToken, verifier);
	}

	@Override
	public void signRequest(Token accessToken, OAuthRequest request) {
		wrappedService.signRequest(accessToken, request);
	}

	@Override
	public String getVersion() {
		return wrappedService.getVersion();
	}

	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return wrappedService.getAuthorizationUrl(requestToken);
	}
}
