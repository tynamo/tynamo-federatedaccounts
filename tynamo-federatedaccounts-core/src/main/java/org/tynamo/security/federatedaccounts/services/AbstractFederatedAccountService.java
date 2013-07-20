package org.tynamo.security.federatedaccounts.services;

import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;

public abstract class AbstractFederatedAccountService implements FederatedAccountService {

	protected final Map<String, Object> entityTypesByRealm;
	protected final Class<?> singleEntityType;
	protected final Logger logger;
	protected final String localAccountRealmName;

	public AbstractFederatedAccountService(Logger logger,
		@Symbol(FederatedAccountSymbols.LOCALACCOUNT_REALMNAME) String localAccountRealmName,
		Map<String, Object> entityTypesByRealm) {
		this.logger = logger;
		this.localAccountRealmName = localAccountRealmName;
		this.entityTypesByRealm = entityTypesByRealm;
		singleEntityType = entityTypesByRealm.containsKey("*") ? (Class<?>) entityTypesByRealm.get("*") : null;
	}

	protected final Map<String, Object> getEntityTypesByRealm() {
		return entityTypesByRealm;
	}

	protected FederatedAccount createLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal,
		Object remoteAccount) {
		// You could also merge the account if there's already an existing user with the same email/username as
		// in the remoteAccount
		FederatedAccount localAccount = null;
		try {
			localAccount = (FederatedAccount) entityType.newInstance();
			// InstantiationException, IllegalAccessException
		} catch (Exception e) {
			throw new AuthenticationException("Entity of type " + localAccount.getClass().getSimpleName()
				+ " is not an instance of " + FederatedAccount.class.getSimpleName(), e);
		}
		localAccount.federate(realmName, remotePrincipal, remoteAccount);
		// new account, always save
		saveAccount(localAccount);
		return localAccount;
	}

	protected abstract void saveAccount(FederatedAccount account);

	protected boolean updateLocalAccount(FederatedAccount localUser, String realmName, Object remotePrincipal,
		Object remoteAccount) {
		boolean modified = localUser.federate(realmName, null, remoteAccount);
		if (modified) updateAccount(localUser);
		return modified;
	}

	protected abstract void updateAccount(FederatedAccount account);

	protected abstract FederatedAccount findLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal,
		Object remoteAccount);

	public AuthenticationInfo federate(String realmName, Object remotePrincipal, AuthenticationToken authenticationToken,
		Object remoteAccount) {
		Class<?> entityType = singleEntityType == null ? (Class<?>) entityTypesByRealm.get(realmName) : singleEntityType;
		if (entityType == null) return null;

		FederatedAccount localAccount = findLocalAccount(entityType, realmName, remotePrincipal, remoteAccount);
		if (localAccount == null) localAccount = createLocalAccount(entityType, realmName, remotePrincipal, remoteAccount);
		else updateLocalAccount(localAccount, realmName, null, remoteAccount);

		if (localAccount.isAccountLocked()) { throw new LockedAccountException("Federated account [" + remotePrincipal
			+ "] is locked."); }
		if (localAccount.isCredentialsExpired()) {
			String msg = "The credentials for federated account [" + remotePrincipal + "] are expired";
			throw new ExpiredCredentialsException(msg);
		}
		SimplePrincipalCollection principalCollection;
		if (localAccountRealmName.isEmpty()) principalCollection = new SimplePrincipalCollection(remotePrincipal, realmName);
		else {
			principalCollection = new SimplePrincipalCollection(localAccount.getLocalAccountPrimaryPrincipal(),
				localAccountRealmName);
			principalCollection.add(remotePrincipal, realmName);
		}
		principalCollection.add(authenticationToken, realmName);
		return new SimpleAuthenticationInfo(principalCollection, authenticationToken.getCredentials());

		// SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(remotePrincipal,
		// authenticationToken.getCredentials(), realmName);
	}
}