package org.tynamo.security.federatedaccounts.facebook;

import java.util.Collection;
import java.util.List;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.PrincipalCollection;
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

	/**
	 * FIXME The following operations should all be removed - https://issues.apache.org/jira/browse/SHIRO-231 requires
	 * AuthenticatingRealm to implement Authorizer, which is wrong. Remove when upgrading Shiro dependency to 1.2
	 */
	@Override
	public boolean isPermitted(PrincipalCollection principals, String permission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPermitted(PrincipalCollection subjectPrincipal, Permission permission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean[] isPermitted(PrincipalCollection subjectPrincipal, String... permissions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean[] isPermitted(PrincipalCollection subjectPrincipal, List<Permission> permissions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPermittedAll(PrincipalCollection subjectPrincipal, String... permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPermittedAll(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkPermissions(PrincipalCollection subjectPrincipal, String... permissions) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkPermissions(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {
		// TODO Auto-generated method stub

	}

}
