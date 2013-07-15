package org.tynamo.security.federatedaccounts.twitter.pages;

import java.net.URL;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.base.AbstractOauthPage;
import org.tynamo.security.federatedaccounts.twitter.TwitterAccessToken;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOauth extends AbstractOauthPage {
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
	private TwitterFactory twitterFactory;

	// private String returnUri;

	protected TwitterFactory getTwitterFactory() {
		return twitterFactory;
	}

	@Override
	protected Object onOauthActivate(EventContext eventContext) throws Exception {
		if (eventContext.getCount() > 2) {
			String action = eventContext.get(String.class, 1);
			// pass along this redirectUrl
			setReturnUri(eventContext.get(String.class, 2));
			if ("request_token".equals(action)) {
				Twitter twitter = getTwitterFactory().getInstance();
				twitter.setOAuthConsumer(getOauthClientId(), getOauthClientSecret());
				return new URL(twitter.getOAuthRequestToken(getOauthRedirectLink(getWindowMode(), getReturnUri()))
					.getAuthenticationURL());
			}
		}

		// capture the redirect url again
		if (eventContext.getCount() > 1) setReturnUri(eventContext.get(String.class, 1));

		String oauth_token = request.getParameter("oauth_token");
		String oauth_verifier = request.getParameter("oauth_verifier");
		if (oauth_verifier == null) {
			alertManager.error("No Oauth verifier code provided");
			return null;
		}

		Twitter twitter = getTwitterFactory().getInstance();
		twitter.setOAuthConsumer(getOauthClientId(), getOauthClientSecret());
		AccessToken accessToken = twitter.getOAuthAccessToken(new RequestToken(oauth_token, getOauthClientSecret()),
			oauth_verifier);
		TwitterAccessToken twitterAccessToken = new TwitterAccessToken(accessToken);
		twitterAccessToken.setRememberMe(isRememberMe());

		try {
			SecurityUtils.getSubject().login(twitterAccessToken);
			alertManager.success("User successfully authenticated");
			setOauthAuthenticated(true);
		} catch (AuthenticationException e) {
			logger
				.error("Using access token " + accessToken + "\nCould not sign in a Twitter federated user because of: ", e);
			// FIXME Deal with other account exception types like expired and
			// locked
			alertManager.error("A Twitter federated user cannot be signed in, report this to support.\n " + e.getMessage());
		}
		return null;
	}
}
