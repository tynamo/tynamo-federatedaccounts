package org.tynamo.security.federatedaccounts.facebook.pages;

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
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.esxx.js.protocol.GAEConnectionManager;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.base.AbstractOauthPage;
import org.tynamo.security.federatedaccounts.facebook.FacebookAccessToken;

public class FacebookOauth extends AbstractOauthPage {
	@Inject
	@Symbol(FederatedAccountSymbols.HTTPCLIENT_ON_GAE)
	private boolean httpClientOnGae;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Inject
	private AlertManager alertManager;

	@Inject
	private PageRenderLinkSource linkSource;

	protected Object onOauthActivate(EventContext eventContext) throws MalformedURLException {
		String code = request.getParameter("code");

		if (eventContext.getCount() > 1) setReturnUri(eventContext.get(String.class, 1));

		if (code == null) {
			alertManager.error("No Oauth authentication code provided");
			return null;
		}

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("client_id", getOauthClientId()));
		qparams.add(new BasicNameValuePair("redirect_uri", getOauthRedirectLink(getWindowMode(), getReturnUri())));
		qparams.add(new BasicNameValuePair("client_secret", getOauthClientSecret()));
		qparams.add(new BasicNameValuePair("code", code));
		HttpGet get = null;
		String accessToken = "";
		long expires = 0L;
		try {
			URI uri = URIUtils.createURI("https", "graph.facebook.com", -1, "/oauth/access_token",
				URLEncodedUtils.format(qparams, "UTF-8"), null);
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
				alertManager.error("Facebook access_token request failed with status code: " + status);
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				long len = entity.getContentLength();
				if (len != -1 && len < 2048) accessToken = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			logger.error("Facebook access_token request failed because of:", e);
			alertManager.error("Facebook access_token request failed with message: " + e.getMessage());
			return null;
		} finally {
			if (get != null) get.abort();
		}

		try {
			if (!accessToken.startsWith("access_token")) throw new IllegalArgumentException();
			// access_token is of form:
			// access_token=119507274749030|2.1AptZFp9__qW3k2PuG4bVA__.3600.1274914800-539598633|9aTyryhVl8vnn3ulLy2w6Txo92E.&expires=4059
			accessToken = accessToken.substring(accessToken.indexOf("=") + 1);
			expires = Long.valueOf(accessToken.substring(accessToken.lastIndexOf("=") + 1));
			accessToken = accessToken.substring(0, accessToken.indexOf("&expires"));
		} catch (Exception e) {
			logger.error("access_token wasn't of right format");
			alertManager.error("Facebook access_token wasn't of right format");
			return null;
		}

		FacebookAccessToken fbAccessToken = new FacebookAccessToken(accessToken, expires);
		fbAccessToken.setRememberMe(isRememberMe());
		try {
			SecurityUtils.getSubject().login(fbAccessToken);
			alertManager.success("User successfully authenticated");
			setOauthAuthenticated(true);
		} catch (AuthenticationException e) {
			logger.error("Using access token " + accessToken + "\nCould not sign in a Facebook federated user because of: ",
				e);
			// FIXME Deal with other account exception types like expired and
			// locked
			alertManager.error("A Facebook federated user cannot be signed in, report this to support.\n "
				+ e.getMessage());
			return null;
		}
		return null;
	}
}
