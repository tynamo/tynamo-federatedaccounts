package org.tynamo.security.federatedaccounts.oauth;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.model.*;
import org.tynamo.security.federatedaccounts.services.TynamoOAuthService;

public class Google20Service implements TynamoOAuthService {
	private static final String API_NAME = "google";
	private static final String VERSION = "2.0";

	private final Google20Api api = new Google20Api();
	private final OAuthConfig config;
	private final Class callbackPageClass;

	public Google20Service(GoogleOAuth20Config config, Class callbackPageClass) {
		this.config = config;
		this.callbackPageClass = callbackPageClass;
	}

	@Override
	public String getApiName() {
		return API_NAME;
	}

	@Override
	public Token getRequestToken() {
		throw new UnsupportedOperationException("Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");
	}

	@Override
	public Token getAccessToken(Token token, Verifier verifier) {
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
		request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
		request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
		request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
		request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());

		request.addBodyParameter("grant_type", "authorization_code"); // todo refactor to API with hook method

		if (config.hasScope()) request.addQuerystringParameter(OAuthConstants.SCOPE, config.getScope());
		Response response = request.send();
		return api.getAccessTokenExtractor().extract(response.getBody());
	}

	@Override
	public void signRequest(Token accessToken, OAuthRequest request) {
		request.addHeader("Authorization", "OAuth " + accessToken.getToken()); // todo refactor to API with hook method
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String getAuthorizationUrl(Token token) {
		return api.getAuthorizationUrl(config);
	}

	@Override
	public AuthenticationToken getAuthenticationToken(Token accessToken) {
		return new GoogleAuthenticationToken(accessToken, 3920);
	}

	@Override
	public Object callback() {
		return callbackPageClass;
	}
}
