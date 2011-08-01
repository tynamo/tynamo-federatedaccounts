package org.tynamo.security.federatedaccounts.twitter.services;

import java.util.Collection;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.PrincipalCollection;
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
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

		AccessToken accessToken = (AccessToken) authenticationToken.getPrincipal();
		
		Twitter twitter = new TwitterFactory().getInstance(accessToken);
		
		User twitterUser;
		try {
			twitterUser = twitter.verifyCredentials();
		} catch (TwitterException e) {
			logger.error(e.getMessage(), e);
			throw new IncorrectCredentialsException("Twitter security verification failed, terminating authentication request", e);
		}

		String principalValue = null;
		switch (principalProperty) {
			case id: principalValue = String.valueOf(twitterUser.getId());
				break;
			case screenname: principalValue = twitterUser.getScreenName();
				break;
			case name: principalValue = twitterUser.getName();
				break;
		}

		AuthenticationInfo authenticationInfo = federatedAccountService.federate(FederatedAccount.Type.twitter.name(), principalValue,
			authenticationToken, twitterUser);
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

	/**
	 * FIXME The following operations should all be removed - https://issues.apache.org/jira/browse/SHIRO-231 requires
	 * AuthenticatingRealm to implement Authorizer, which is wrong. Remove when upgrading Shiro dependency to 1.2
	 */
	@Override
	public boolean isPermitted(PrincipalCollection principals, String permission) {
		return false;
	}

	@Override
	public boolean isPermitted(PrincipalCollection subjectPrincipal, Permission permission) {
		return false;
	}

	@Override
	public boolean[] isPermitted(PrincipalCollection subjectPrincipal, String... permissions) {
		return null;
	}

	@Override
	public boolean[] isPermitted(PrincipalCollection subjectPrincipal, List<Permission> permissions) {
		return null;
	}

	@Override
	public boolean isPermittedAll(PrincipalCollection subjectPrincipal, String... permissions) {
		return false;
	}

	@Override
	public boolean isPermittedAll(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) {
		return false;
	}

	@Override
	public void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {

	}

	@Override
	public void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException {

	}

	@Override
	public void checkPermissions(PrincipalCollection subjectPrincipal, String... permissions) throws AuthorizationException {

	}

	@Override
	public void checkPermissions(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) throws AuthorizationException {

	}

	@Override
	public boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
		return false;
	}

	@Override
	public boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
		return null;
	}

	@Override
	public boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
		return false;
	}

	@Override
	public void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {

	}

	@Override
	public void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {

	}

	@Override
	public void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {

	}

}
