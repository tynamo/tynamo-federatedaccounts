package org.tynamo.security.federatedaccounts.services;

import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;

public class DefaultHibernateFederatedAccountServiceImpl extends AbstractFederatedAccountService implements
	FederatedAccountService {

	private final Session session;

	public DefaultHibernateFederatedAccountServiceImpl(Logger logger,
		@Symbol(FederatedAccountSymbols.LOCALACCOUNT_REALMNAME) String localAccountRealmName, Session session,
		Map<String, Object> entityTypesByRealm) {
		super(logger, localAccountRealmName, entityTypesByRealm);
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
		if (!entityTypesByRealm.containsKey(realmName + IDPROPERTY)) {
			logger
				.warn(String
					.format(
						"Local account cannot be found. There's no property name configured for the federated realm attribute key %s.id and entity type %s",
						realmName + IDPROPERTY, entityType.getSimpleName()));
			return null;
		}

		return (FederatedAccount) session.createCriteria(entityType)
			.add(Restrictions.eq((String) entityTypesByRealm.get(realmName + IDPROPERTY), remotePrincipal)).uniqueResult();
	}

}
