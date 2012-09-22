package org.tynamo.security.federatedaccounts.services;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.tynamo.security.federatedaccounts.FederatedAccount;

public class DefaultHibernateFederatedAccountServiceImpl extends AbstractFederatedAccountService implements
	FederatedAccountService {

	private final Session session;

	public DefaultHibernateFederatedAccountServiceImpl(Session session, Map<String, Object> entityTypesByRealm) {
		super(entityTypesByRealm);
		this.session = session;
	}

	protected final Session getSession() {
		return session;
	}

	protected void saveAccount(FederatedAccount localAccount) {
		session.save(localAccount);
	}

	protected void updateAccount(FederatedAccount localUser) {
		session.update(localUser);
	}

	protected FederatedAccount findLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal,
		Object remoteAccount) {
		return (FederatedAccount) session.createCriteria(entityType)
			.add(Restrictions.eq((String) entityTypesByRealm.get(realmName + IDPROPERTY), remotePrincipal)).uniqueResult();
	}

}
