package org.tynamo.security.rollingtokens.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.rollingtokens.entities.ExpiringRollingToken;

// instead of extending, this should be a delegating realm, requiring the primary realm so it can query
// the realm name and use its authorization if it so wishes
public class RollingTokenRealm extends AuthenticatingRealm {

	public static final String NAME = "rollingtokens";
	private final EntityManager entityManager;
	private final Logger logger;
	private FederatedAccountService federatedAccountService;

	public RollingTokenRealm(Logger logger, EntityManager entityManager, FederatedAccountService federatedAccountService) {
		this.logger = logger;
		this.entityManager = entityManager;
		this.federatedAccountService = federatedAccountService;
		setName(NAME);
		this.setAuthenticationTokenClass(RollingToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// FIXME this probably shouldn't silently fail - it seems to invalidate even the existing subject
		RollingToken upToken = (RollingToken) token;

		Object principal = token.getPrincipal();

		// Null username is invalid
		if (principal == null) {
			logger.warn("Null usernames are not allowed by this realm.");
			return null;
		}

		String hostAddress = upToken.getHost();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExpiringRollingToken> criteriaQuery = builder.createQuery(ExpiringRollingToken.class);
		Root<ExpiringRollingToken> from = criteriaQuery.from(ExpiringRollingToken.class);

		Predicate predicate = builder.and(builder.equal(from.get("name"), principal.toString()),
			builder.greaterThan(from.<Date> get("expiresAfter"), new Date()),
			builder.equal(from.get("hostAddress"), hostAddress));

		List<ExpiringRollingToken> rollingTokens = entityManager.createQuery(criteriaQuery.where(predicate))
			.getResultList();

		if (rollingTokens.size() <= 0) return null;

		// FIXME might need to pass a list of rolling tokens
		return federatedAccountService.federate(getName(), principal, token, null);
	}

}
