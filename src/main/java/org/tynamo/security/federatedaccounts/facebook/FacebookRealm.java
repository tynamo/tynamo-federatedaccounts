package org.tynamo.security.federatedaccounts.facebook;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
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
	private Logger logger;

	private FederatedAccountService federatedAccountService;

	public FacebookRealm(Logger logger, FederatedAccountService federatedAccountService) {
		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;
		setName("facebook");
		setAuthenticationTokenClass(FacebookConnectToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		FacebookConnectToken token = (FacebookConnectToken) authenticationToken;

		FacebookClient facebookClient = new DefaultFacebookClient(authenticationToken.getCredentials().toString());
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

		return federatedAccountService.federate(FederatedAccount.Type.facebook.name(), facebookUser.getId(), authenticationToken, facebookUser);
		// Account federatedAccount = federatedAccountService.findById(FederatedAccount.Type.facebook,
		// facebookUser.getId());
		// if (federatedAccountService.isSynchLocalAccount()) {
		// if (federatedAccount == null) federatedAccount =
		// federatedAccountService.createLocalAccount(FederatedAccount.Type.facebook,
		// facebookUser);
		// else federatedAccountService.updateLocalAccount(FederatedAccount.Type.facebook, federatedAccount, facebookUser);
		// }

		// {
		// try {
		// session.update(user);
		// } catch (Exception e) {
		// logger.warn("Will try again next time. Couldn't update facebook user account because of: ", e);
		// }
		// }

		// if (federatedAccount.isAccountLocked()) { throw new LockedAccountException("Facebook federated account ["
		// + federatedAccount.getUsername() + "] is locked."); }
		// if (federatedAccount.isCredentialsExpired()) {
		// String msg = "The credentials for account [" + facebookUser.getId() + "] are expired";
		// throw new ExpiredCredentialsException(msg);
		// }
		// return new SimpleAuthenticationInfo(federatedAccount.getUsername(), token.getCredentials(), getName());

		// return federatedAccount;
	}

	public static final class FacebookSymbols {

		public static final String FACEBOOK_SECRET = "test";
		public static final String FACEBOOK_API_KEY = "test";
		public static final String FACEBOOK_CANVAS_URL = "test";

	}

}
