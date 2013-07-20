package org.tynamo.security.federatedaccounts.testapp.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.tynamo.security.federatedaccounts.testapp.entities.User;
import org.tynamo.security.federatedaccounts.testapp.entities.User.Role;

public class UserRealm extends AuthorizingRealm {
	protected final EntityManager em;

	public UserRealm(EntityManager em) {
		super(new MemoryConstrainedCacheManager());
		this.em = em;
		setName("localaccounts");
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		setCredentialsMatcher(new HashedCredentialsMatcher(Sha1Hash.ALGORITHM_NAME));
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals == null) throw new AuthorizationException("PrincipalCollection was null, which should not happen");

		if (principals.isEmpty()) return null;

		if (principals.fromRealm(getName()).size() <= 0) return null;

		String username = (String) principals.fromRealm(getName()).iterator().next();
		if (username == null) return null;
		User user = findByUsername(username);
		if (user == null) return null;
		Set<String> roles = new HashSet<String>(user.getRoles().size());
		for (Role role : user.getRoles())
			roles.add(role.name());
		return new SimpleAuthorizationInfo(roles);
	}

	private User findByUsername(String username) {
		CriteriaBuilder qb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = qb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		List<User> results = em.createQuery(query.where(qb.equal(root.get("username"), username))).getResultList();
		return results.size() <= 0 ? null : results.get(0);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		String username = upToken.getUsername();

		// Null username is invalid
		if (username == null) { throw new AccountException("Null usernames are not allowed by this realm."); }

		User user = findByUsername(username);
		if (user.getFacebookUserId() != null) { throw new AccountException("Account [" + username
				+ "] is federated with Facebook and cannot be locally authenticated."); }

		if (user.isAccountLocked()) { throw new LockedAccountException("Account [" + username + "] is locked."); }
		if (user.isCredentialsExpired()) {
			String msg = "The credentials for account [" + username + "] are expired";
			throw new ExpiredCredentialsException(msg);
		}
		return new SimpleAuthenticationInfo(username, user.getEncodedPassword(), new SimpleByteSource(user.getPasswordSalt()), getName());
	}

}
