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
import org.tynamo.security.federatedaccounts.twitter.TwitterAuthenticationToken;

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

	private FederatedAccountService federatedAccountService;

	public TwitterRealm(Logger logger, FederatedAccountService federatedAccountService,
		@Inject @Symbol(TwitterRealm.TWITTER_PRINCIPAL) String principalPropertyName) {
		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;
		// Let this throw IllegalArgumentException if value is not supported
		this.principalProperty = PrincipalProperty.valueOf(principalPropertyName);
		setName(FederatedAccount.Type.twitter.name());
		setAuthenticationTokenClass(TwitterAuthenticationToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
		throws AuthenticationException {

		AccessToken accessToken = (AccessToken) authenticationToken.getPrincipal();

		Twitter twitter = new TwitterFactory().getInstance(accessToken);

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

		AuthenticationInfo authenticationInfo = federatedAccountService.federate(FederatedAccount.Type.twitter.name(),
			principalValue, authenticationToken, twitterUser);
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
