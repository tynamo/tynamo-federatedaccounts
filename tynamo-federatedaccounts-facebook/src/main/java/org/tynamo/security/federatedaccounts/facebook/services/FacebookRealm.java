package org.tynamo.security.federatedaccounts.facebook.services;

import org.apache.shiro.authc.AccountException;
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
import org.tynamo.security.federatedaccounts.facebook.FacebookAccessToken;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;

/**
 * <p>
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Facebook.
 */
public class FacebookRealm extends AuthenticatingRealm {
	public static final String FACEBOOK_CLIENTID = "facebook.clientid";
	public static final String FACEBOOK_CLIENTSECRET = "facebook.clientsecret";
	public static final String FACEBOOK_PERMISSIONS = "facebook.permissions";
	public static final String FACEBOOK_PRINCIPAL = "facebook.principal";

	private Logger logger;

	public static enum PrincipalProperty {
		id, email, name
	}

	private PrincipalProperty principalProperty;

	private FederatedAccountService federatedAccountService;

	public FacebookRealm(Logger logger, FederatedAccountService federatedAccountService,
			@Inject @Symbol(FacebookRealm.FACEBOOK_PRINCIPAL) String principalPropertyName) {
		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;
		// Let this throw IllegalArgumentException if value is not supported
		this.principalProperty = PrincipalProperty.valueOf(principalPropertyName);
		setName(FederatedAccount.FederatedAccountType.facebook.name());
		setAuthenticationTokenClass(FacebookAccessToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		FacebookAccessToken token = (FacebookAccessToken) authenticationToken;

		FacebookClient facebookClient = new DefaultFacebookClient(authenticationToken.getPrincipal().toString());
		User facebookUser;
		try {
			facebookUser = facebookClient.fetchObject("me", User.class);
		} catch (FacebookException e) {
			logger.error(e.getMessage(), e);
			throw new IncorrectCredentialsException("Facebook security verification failed, terminating authentication request", e);
		}
		// Null username is invalid, throw an exception if so - indicates that user hasn't granted the right
		// permissions (and/or we haven't asked for it)
		if (facebookUser == null) throw new AccountException("Null Facebook user is not allowed by this realm.");
		// long facebookUserId;
		// try {
		// facebookUserId = Long.valueOf(facebookUser.getId());
		// } catch (NumberFormatException e) {
		// logger.error("Facebook implementation has changed, returned id '" + facebookUser.getId() +
		// "' cannot be cast to Long");
		// throw new AccountException("Unknown user id format. Report this problem to support");
		// }

		String principalValue = null;
		switch (principalProperty) {
			case id: principalValue = facebookUser.getId();
				break;
			case email: principalValue = facebookUser.getEmail();
				break;
			case name: principalValue = facebookUser.getName();
				break;
		}

		AuthenticationInfo authenticationInfo = federatedAccountService.federate(FederatedAccount.FederatedAccountType.facebook.name(), principalValue,
				authenticationToken, facebookUser);
		// returned principalcollection is immutable
		// authenticationInfo.getPrincipals().fromRealm(FederatedAccount.Type.facebook.name()).add(authenticationToken);
		return authenticationInfo;
		// if (federatedAccount.isAccountLocked()) { throw new LockedAccountException("Facebook federated account ["
		// + federatedAccount.getUsername() + "] is locked."); }
		// if (federatedAccount.isCredentialsExpired()) {
		// String msg = "The credentials for account [" + facebookUser.getId() + "] are expired";
		// throw new ExpiredCredentialsException(msg);
		// }
		// return new SimpleAuthenticationInfo(federatedAccount.getUsername(), token.getCredentials(), getName());

		// return federatedAccount;
	}
}