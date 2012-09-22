package org.tynamo.security.federatedaccounts.services;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.tynamo.security.federatedaccounts.FederatedAccount;

public class DefaultJpaFederatedAccountServiceImpl extends AbstractFederatedAccountService {

	private final EntityManager entityManager;

	public DefaultJpaFederatedAccountServiceImpl(EntityManager entityManager, Map<String, Object> entityTypesByRealm) {
		super(entityTypesByRealm);
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected FederatedAccount findLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal,
		Object remoteAccount) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();

		Root root = cq.from(entityType);
		cq.where(cb.equal(root.get((String) entityTypesByRealm.get(realmName + IDPROPERTY)), remotePrincipal));

		TypedQuery<FederatedAccount> query = entityManager.createQuery(cq.select(root));
		List<FederatedAccount> results = query.getResultList();
		return results.size() <= 0 ? null : results.get(0);
	}

	protected void saveAccount(FederatedAccount account) {
		entityManager.persist(account);
	}

	@Override
	protected void updateAccount(FederatedAccount account) {
		entityManager.merge(account);
	}
}
