package org.tynamo.security.federatedaccounts.services;

import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.tynamo.security.federatedaccounts.FederatedAccount;

public class DefaultHibernateFederatedAccountServiceImpl implements FederatedAccountService {
	private final Map<String, Object> entityTypesByRealm;
	private final Class<?> singleEntityType;
	private final Session session;

	public DefaultHibernateFederatedAccountServiceImpl(Session session, Map<String, Object> entityTypesByRealm) {
		this.entityTypesByRealm = entityTypesByRealm;
		singleEntityType = entityTypesByRealm.containsKey("*") ? (Class<?>) entityTypesByRealm.get("*") : null;
		this.session = session;

	}

	protected final Session getSession() {
		return session;
	}

	protected final Map<String, Object> getEntityTypesByRealm() {
		return entityTypesByRealm;
	}

	protected FederatedAccount createLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal, Object remoteAccount) {
		// You could also merge the account if there's already an existing user with the same email/username as
		// in the remoteAccount
		FederatedAccount localAccount = null;
		try {
			localAccount = (FederatedAccount) entityType.newInstance();
			// InstantiationException, IllegalAccessException
		} catch (Exception e) {
			throw new AuthenticationException("Entity of type " + localAccount.getClass().getSimpleName() + " is not an instance of "
					+ FederatedAccount.class.getSimpleName(), e);
		}
		localAccount.federate(realmName, remotePrincipal, remoteAccount);
		// new account, always save
		session.save(localAccount);
		return localAccount;
	}

	protected boolean updateLocalAccount(FederatedAccount localUser, String realmName, Object remotePrincipal, Object remoteAccount) {
		boolean modified = localUser.federate(realmName, null, remoteAccount);
		if (modified) session.update(localUser);
		return modified;
	}

	FederatedAccount findLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal, Object remoteAccount) {
		return (FederatedAccount) session.createCriteria(entityType)
				.add(Restrictions.eq((String) entityTypesByRealm.get(realmName + IDPROPERTY), remotePrincipal)).uniqueResult();
	}

	@Override
	public AuthenticationInfo federate(String realmName, Object remotePrincipal, AuthenticationToken authenticationToken, Object remoteAccount) {
		Class<?> entityType = singleEntityType == null ? (Class<?>) entityTypesByRealm.get(realmName) : singleEntityType;
		if (entityType == null) return null;
		FederatedAccount localAccount = findLocalAccount(entityType, realmName, remotePrincipal, remoteAccount);
		if (localAccount == null) localAccount = createLocalAccount(entityType, realmName, remotePrincipal, remoteAccount);
		else updateLocalAccount(localAccount, realmName, null, remoteAccount);
		if (localAccount.isAccountLocked()) { throw new LockedAccountException("Federated account [" + remotePrincipal + "] is locked."); }
		if (localAccount.isCredentialsExpired()) {
			String msg = "The credentials for federated account [" + remotePrincipal + "] are expired";
			throw new ExpiredCredentialsException(msg);
		}

		return new SimpleAuthenticationInfo(remotePrincipal, authenticationToken.getCredentials(), realmName);
	}
}
