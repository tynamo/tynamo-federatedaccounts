package org.tynamo.security.federatedaccounts.base;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.components.FlashMessager;
import org.tynamo.security.federatedaccounts.facebook.FacebookConnectToken;

public abstract class AbstractFacebookOauth extends FacebookOauthRedirectBase {
	public static final String FACEBOOK_CLIENTID = "facebook.clientid";
	public static final String FACEBOOK_CLIENTSECRET = "facebook.clientsecret";
	public static final String FACEBOOK_PERMISSIONS = "facebook.permissions";

	@Inject
	@Symbol(FACEBOOK_CLIENTID)
	private String clientId;

	@Inject
	@Symbol(FACEBOOK_CLIENTSECRET)
	private String clientSecret;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Component
	private FlashMessager flashMessager;

	@Inject
	private PageRenderLinkSource linkSource;

	private boolean fbAuthenticated;

	protected void onActivate() throws MalformedURLException {
		String code = request.getParameter("code");
		if (code == null) {
			flashMessager.setFailureMessage("No Oauth authentication code provided");
			return;
		}

		// String accessTokenUri = "https://graph.facebook.com/oauth/access_token?client_id=" + clientId
		// + "&redirect_uri=http://localhost:8080/oauth&client_secret=" + clientSecret + "&code=" + code;
		String accessTokenUri = "https://graph.facebook.com/oauth/access_token";

		// String accessTokenUri = "https://graph.facebook.com/oauth/access_token?client_id&#61;" + clientId
		// + "&amp;redirect_uri&#61;http://localhost:8080/oauth&amp;client_secret&#61;" + clientSecret + "&amp;code&#61;" +
		// code;
		// https://graph.facebook.com/oauth/access_token?client_id=119507274749030&redirect_uri=http://localhost:8080/oauth&client_secret=d8b3b7dc6d5b6ddaebd68549002d643d&code=2._ViV33QBoP0Yhzmua_6gvA__.3600.1274994000-539598633|7_yFQgTwoBoD7DJYx8dqMhiXiP0.
		// logger.info("access token uri " + accessTokenUri);
		GetMethod get = new GetMethod(accessTokenUri);
		NameValuePair[] queryString = new NameValuePair[4];
		queryString[0] = new NameValuePair("client_id", clientId);
		queryString[1] = new NameValuePair("redirect_uri", getOauthRedirectLink());
		queryString[2] = new NameValuePair("client_secret", clientSecret);
		queryString[3] = new NameValuePair("code", code);
		get.setQueryString(queryString);
		HttpClient httpClient = new HttpClient();
		String accessToken = "";
		int expires = 0;
		try {
			httpClient.executeMethod(get);
			accessToken = get.getResponseBodyAsString(1000);
		} catch (Exception e) {
			logger.error("Facebook access_token request failed because of:", e);
			flashMessager.setFailureMessage("Facebook access_token request failed with message: " + e.getMessage());
			return;
		} finally {
			get.releaseConnection();
		}

		int status = get.getStatusCode();
		if (status != HttpStatus.SC_OK) {
			logger.error("Facebook access_token request returned status code " + status);
			flashMessager.setFailureMessage("Facebook access_token request failed with status code: " + status);
			return;
		}
		try {
			if (!accessToken.startsWith("access_token")) throw new IllegalArgumentException();
			// access_tokein is of form:
			// access_token=119507274749030|2.1AptZFp9__qW3k2PuG4bVA__.3600.1274914800-539598633|9aTyryhVl8vnn3ulLy2w6Txo92E.&expires=4059
			accessToken = accessToken.substring(accessToken.indexOf("=") + 1);
			expires = Integer.valueOf(accessToken.substring(accessToken.lastIndexOf("=") + 1));
			accessToken = accessToken.substring(0, accessToken.indexOf("&expires"));
		} catch (Exception e) {
			logger.error("access_token wasn't of right format");
			flashMessager.setFailureMessage("Facebook access_token wasn't of right format");
			return;
		}

		try {
			SecurityUtils.getSubject().login(new FacebookConnectToken(code, accessToken));
			flashMessager.setSuccessMessage("User successfully authenticated");
			fbAuthenticated = true;
		} catch (AuthenticationException e) {
			logger.error("Using access token " + accessToken + "\nCould not sign in a Facebook federated user because of: ", e);
			// FIXME Deal with other account exception types like expired and
			// locked
			flashMessager.setFailureMessage("A Facebook federated user cannot be signed in, report this to support");
		}
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (fbAuthenticated) javaScriptSupport.addScript("authenticationExecuted(true);");
	}
}
