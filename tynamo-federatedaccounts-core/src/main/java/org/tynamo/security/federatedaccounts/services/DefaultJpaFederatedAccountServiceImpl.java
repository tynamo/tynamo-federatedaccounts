package org.tynamo.security.federatedaccounts.services;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
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

		TypedQuery<?> query = entityManager.createQuery(cq.select(root));

		try {
			return (FederatedAccount) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	protected void saveAccount(FederatedAccount account) {
		merge(account);
	}

	@Override
	protected void updateAccount(FederatedAccount account) {
		merge(account);
	}

	private void merge(FederatedAccount account) {
		EntityTransaction tx = entityManager.getTransaction();

		if (!tx.isActive()) {
			tx.begin();
			entityManager.merge(account);
			tx.commit();
		} else {
			entityManager.merge(account);
		}
	};

}
