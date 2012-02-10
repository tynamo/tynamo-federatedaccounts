package org.tynamo.security.federatedaccounts.twitter.services;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.federatedaccounts.twitter.TwitterAccessToken;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * <p>
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Twitter.
 */
public class TwitterRealm extends AuthenticatingRealm {
	public static final String TWITTER_CLIENTID = "twitter.clientid";
	public static final String TWITTER_CLIENTSECRET = "twitter.clientsecret";
	public static final String TWITTER_PRINCIPAL = "twitter.principal";

	private Logger logger;

	public static enum PrincipalProperty {
		id, screenname, name
	}

	private PrincipalProperty principalProperty;

	private final FederatedAccountService federatedAccountService;
	private final TwitterFactory twitterFactory;
	private String oauthClientId;
	private String oauthClientSecret;

	public TwitterRealm(Logger logger, FederatedAccountService federatedAccountService, TwitterFactory twitterFactory,
		@Inject @Symbol(TwitterRealm.TWITTER_CLIENTID) String oauthClientId,
		@Inject @Symbol(TwitterRealm.TWITTER_CLIENTSECRET) String oauthClientSecret,
		@Inject @Symbol(TwitterRealm.TWITTER_PRINCIPAL) String principalPropertyName) {
		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.twitterFactory = twitterFactory;
		this.oauthClientId = oauthClientId;
		this.oauthClientSecret = oauthClientSecret;
		this.logger = logger;
		// Let this throw IllegalArgumentException if value is not supported
		this.principalProperty = PrincipalProperty.valueOf(principalPropertyName);
		setName(FederatedAccount.FederatedAccountType.twitter.name());
		setAuthenticationTokenClass(TwitterAccessToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
		throws AuthenticationException {
		AccessToken accessToken = (AccessToken) authenticationToken.getCredentials();

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(oauthClientId, oauthClientSecret);
		twitter.setOAuthAccessToken(accessToken);

		User twitterUser;
		try {
			twitterUser = twitter.verifyCredentials();
		} catch (TwitterException e) {
			logger.error(e.getMessage(), e);
			throw new IncorrectCredentialsException(
				"Twitter security verification failed, terminating authentication request", e);
		}

		String principalValue = null;
		switch (principalProperty) {
		case id:
			principalValue = String.valueOf(twitterUser.getId());
			break;
		case screenname:
			principalValue = twitterUser.getScreenName();
			break;
		case name:
			principalValue = twitterUser.getName();
			break;
		}

		AuthenticationInfo authenticationInfo = federatedAccountService.federate(
			FederatedAccount.FederatedAccountType.twitter.name(), principalValue, authenticationToken, twitterUser);
		// returned principalcollection is immutable
		// authenticationInfo.getPrincipals().fromRealm(FederatedAccount.Type.twitter.name()).add(authenticationToken);
		return authenticationInfo;
		// if (federatedAccount.isAccountLocked()) { throw new LockedAccountException("Twitter federated account ["
		// + federatedAccount.getUsername() + "] is locked."); }
		// if (federatedAccount.isCredentialsExpired()) {
		// String msg = "The credentials for account [" + twitterUser.getId() + "] are expired";
		// throw new ExpiredCredentialsException(msg);
		// }
		// return new SimpleAuthenticationInfo(federatedAccount.getUsername(), token.getCredentials(), getName());

		// return federatedAccount;
	}
}
