package org.tynamo.security.federatedaccounts.base;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.esxx.js.protocol.GAEConnectionManager;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.components.FlashMessager;
import org.tynamo.security.federatedaccounts.oauth.tokens.FacebookAuthenticationToken;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public abstract class AbstractFacebookOauthPage extends FacebookOauthComponentBase {
	@Inject
	@Symbol(FederatedAccountSymbols.HTTPCLIENT_ON_GAE)
	private boolean httpClientOnGae;

	@Inject
	@Symbol(FederatedAccountSymbols.SUCCESSURL)
	private String successUrl;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Component
	private FlashMessager flashMessager;

	@Inject
	private PageRenderLinkSource linkSource;

	private boolean fbAuthenticated;

	@Property
	private WindowMode windowMode;

	protected void onActivate(String windowModeText) throws MalformedURLException {
		try {
			windowMode = WindowMode.valueOf(windowModeText);
		} catch (IllegalArgumentException e) {
		}

		String code = request.getParameter("code");
		if (code == null) {
			flashMessager.setFailureMessage("No Oauth authentication code provided");
			return;
		}

		// String accessTokenUri = "https://graph.facebook.com/oauth/access_token?client_id=" + clientId
		// + "&redirect_uri=http://localhost:8080/oauth&client_secret=" + clientSecret + "&code=" + code;
		// String accessTokenUri = "https://graph.facebook.com/oauth/access_token";

		// String accessTokenUri = "https://graph.facebook.com/oauth/access_token?client_id&#61;" + clientId
		// + "&amp;redirect_uri&#61;http://localhost:8080/oauth&amp;client_secret&#61;" + clientSecret + "&amp;code&#61;" +
		// code;
		// https://graph.facebook.com/oauth/access_token?client_id=119507274749030&redirect_uri=http://localhost:8080/oauth&client_secret=d8b3b7dc6d5b6ddaebd68549002d643d&code=2._ViV33QBoP0Yhzmua_6gvA__.3600.1274994000-539598633|7_yFQgTwoBoD7DJYx8dqMhiXiP0.
		// logger.info("access token uri " + accessTokenUri);

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("client_id", getOauthClientId()));
		qparams.add(new BasicNameValuePair("redirect_uri", getOauthRedirectLink(windowMode)));
		qparams.add(new BasicNameValuePair("client_secret", getOauthClientSecret()));
		qparams.add(new BasicNameValuePair("code", code));
		HttpGet get = null;
		String rawResponse = "";
		Token token = null;
		long expires = 0L;
		try {
			URI uri = URIUtils
					.createURI("https", "graph.facebook.com", -1, "/oauth/access_token", URLEncodedUtils.format(qparams, "UTF-8"), null);
			get = new HttpGet(uri);

			// HttpGet get = new HttpGet(accessTokenUri);
			// NameValuePair[] queryString = new NameValuePair[4];
			// queryString[0] = new NameValuePair("client_id", clientId);
			// queryString[1] = new NameValuePair("redirect_uri", getOauthRedirectLink());
			// queryString[2] = new NameValuePair("client_secret", clientSecret);
			// queryString[3] = new NameValuePair("code", code);
			// get.setQueryString(queryString);
			// HttpClient httpClient = new DefaultHttpClient();
			HttpClient httpClient = httpClientOnGae ? new DefaultHttpClient(new GAEConnectionManager(), new BasicHttpParams())
					: new DefaultHttpClient();

			HttpResponse response = httpClient.execute(get);
			int status = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK != status) {
				logger.error("Facebook access_token request returned status code " + status);
				flashMessager.setFailureMessage("Facebook access_token request failed with status code: " + status);
				return;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				long len = entity.getContentLength();
				if (len != -1 && len < 2048) rawResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			logger.error("Facebook access_token request failed because of:", e);
			flashMessager.setFailureMessage("Facebook access_token request failed with message: " + e.getMessage());
			return;
		} finally {
			if (get != null) get.abort();
		}

		try {
			if (!rawResponse.startsWith("access_token")) throw new IllegalArgumentException();
			// access_token is of form:
			// access_token=119507274749030|2.1AptZFp9__qW3k2PuG4bVA__.3600.1274914800-539598633|9aTyryhVl8vnn3ulLy2w6Txo92E.&expires=4059
			String accessToken = rawResponse.substring(rawResponse.indexOf("=") + 1);
			expires = Long.valueOf(accessToken.substring(accessToken.lastIndexOf("=") + 1));
			accessToken = accessToken.substring(0, accessToken.indexOf("&expires"));
			token = new Token(accessToken, "", rawResponse);
		} catch (Exception e) {
			logger.error("access_token wasn't of right format");
			flashMessager.setFailureMessage("Facebook access_token wasn't of right format");
			return;
		}

		try {
			SecurityUtils.getSubject().login(new FacebookAuthenticationToken(token, expires));
			flashMessager.setSuccessMessage("User successfully authenticated");
			fbAuthenticated = true;
		} catch (AuthenticationException e) {
			logger.error("Using access token " + token.getToken() + "\nCould not sign in a Facebook federated user because of: ", e);
			// FIXME Deal with other account exception types like expired and
			// locked
			flashMessager.setFailureMessage("A Facebook federated user cannot be signed in, report this to support.\n " + e.getMessage());
		}
	}

	@Inject
	private BaseURLSource baseURLSource;

	public String getSuccessLink() {
		return "".equals(successUrl) ? "" : baseURLSource.getBaseURL(request.isSecure()) + successUrl;
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (fbAuthenticated) javaScriptSupport.addScript("onAuthenticationSuccess('" + getSuccessLink() + "', '" + windowMode.name() + "');");
	}
}
