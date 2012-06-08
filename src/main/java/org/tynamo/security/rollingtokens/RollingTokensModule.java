package org.tynamo.security.rollingtokens;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Advise;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.jpa.JpaEntityPackageManager;
import org.apache.tapestry5.jpa.JpaTransactionAdvisor;
import org.apache.tapestry5.services.Cookies;
import org.slf4j.Logger;
import org.tynamo.security.Authenticator;
import org.tynamo.security.rollingtokens.entities.ExpiringRollingToken;
import org.tynamo.security.rollingtokens.internal.RollingTokenAutoLoginAdvice;
import org.tynamo.security.rollingtokens.services.CommittingAuthenticationListener;
import org.tynamo.security.rollingtokens.services.RollingToken;
import org.tynamo.security.rollingtokens.services.RollingTokenRealm;

public class RollingTokensModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, RollingTokenRealm.class).withId(RollingTokenRealm.class.getSimpleName());
		binder.bind(CommittingAuthenticationListener.class, RollingTokenAuthenticationListener.class);
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration, Authenticator authenticator,
		WebSecurityManager securityManager, CommittingAuthenticationListener rollingTokenAuthenticationListener,
		@InjectService("RollingTokenRealm") AuthenticatingRealm rollingTokenRealm) {
		configuration.add(rollingTokenRealm);

		// doesn't necessarily belong here, but we can just as well set up the listener here
		authenticator.addAuthenticationListener(rollingTokenAuthenticationListener);
	}

	@SuppressWarnings("unchecked")
	@Advise(serviceInterface = SubjectFactory.class)
	public static void loginIfRollingTokenMatches(MethodAdviceReceiver receiver, Logger logger,
		HttpServletRequest request, @Inject @Symbol(RollingTokenSymbols.CONFIGURED_REALM) String realmName,
		@Inject @Symbol(RollingTokenSymbols.CONFIGURED_PRINCIPALTYPE) String principalType) throws SecurityException,
		NoSuchMethodException, ClassNotFoundException {
		final RollingTokenAutoLoginAdvice autoLoginAdvice = new RollingTokenAutoLoginAdvice(logger, request, realmName,
			principalType.isEmpty() ? null : Class.forName(principalType));
		receiver.adviseMethod(receiver.getInterface().getMethod("createSubject", SubjectContext.class), autoLoginAdvice);
	}

	@Advise(serviceInterface = CommittingAuthenticationListener.class)
	public static void commitRollingTokenTransactions(JpaTransactionAdvisor advisor, MethodAdviceReceiver receiver) {
		advisor.addTransactionCommitAdvice(receiver);
	}

	@Contribute(JpaEntityPackageManager.class)
	public static void providePackages(Configuration<String> configuration) {
		configuration.add(ExpiringRollingToken.class.getPackage().getName());
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(RollingTokenSymbols.CONFIGURED_REALM, "");
		configuration.add(RollingTokenSymbols.CONFIGURED_PRINCIPALTYPE, "");
	}

	public static class RollingTokenAuthenticationListener implements CommittingAuthenticationListener {
		private static Random random = new Random((new Date()).getTime());

		// TODO we should probably allow supplying this as a symbol
		// In seconds, default is a month
		private int maxAge = 30 * 24 * 3600;

		private final HttpServletRequest request;
		private final HttpServletResponse response;
		private final Cookies cookies;
		private final EntityManager entityManager;
		private String realmName;
		private Class principalType;

		public RollingTokenAuthenticationListener(EntityManager entityManager, Cookies cookies, HttpServletRequest request,
			HttpServletResponse response, @Inject @Symbol(RollingTokenSymbols.CONFIGURED_REALM) String realmName,
			@Inject @Symbol(RollingTokenSymbols.CONFIGURED_PRINCIPALTYPE) String principalType) throws ClassNotFoundException {
			this.entityManager = entityManager;
			this.cookies = cookies;
			this.request = request;
			this.response = response;
			this.realmName = realmName;
			this.principalType = principalType.isEmpty() ? null : Class.forName(principalType);
		}

		@Override
		public void onLogout(PrincipalCollection principals) {
			eraseAllExpiringRollingTokens(principals.byType(RollingToken.class));
			cookies.removeCookieValue(RollingToken.TOKEN_NAME);
		}

		public void onFailure(AuthenticationToken token, AuthenticationException ae) {
			if (token instanceof RollingToken) {
				if (token.getPrincipal() == null || token.getCredentials() == null) return;
				// The token might still exist after it's expired, so clean up
				// ExpiringRollingToken expiringToken = (ExpiringRollingToken) session.createCriteria(ExpiringRollingToken.class)
				// .add(Restrictions.eq("name", token.getPrincipal().toString())).add(Restrictions.eq("value",
				// token.getCredentials().toString()))
				// .uniqueResult();
				// if (expiringToken != null) session.delete(expiringToken);
				Query query = entityManager.createQuery("delete from " + ExpiringRollingToken.class.getSimpleName()
					+ " e where e.name = ?1 and e.value = ?2");
				query.setParameter(1, token.getPrincipal().toString());
				query.setParameter(2, token.getCredentials().toString());
				query.executeUpdate();

				// If this is rememberme authentication, security is run before request and cookie shadows are established
				// so use native servlet request here
				// cookies.removeCookieValue(UsernameRollingToken.TOKEN_NAME);
				Cookie cookie = new Cookie(RollingToken.TOKEN_NAME, "xxx");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}

		@Override
		public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
			// Note that the same token is actually rolling - it creates a new token and "expires" the old one
			// by replacing it. This *may* cause problems and even an infinite loop if you are not careful -
			// with a single refresh, the old token is sent several times with each request
			if (token instanceof RollingToken) return;
			if (!(token instanceof RememberMeAuthenticationToken)) return;
			RememberMeAuthenticationToken rememberMeToken = (RememberMeAuthenticationToken) token;
			if (rememberMeToken.isRememberMe()) {
				String tokenValue = (new BigInteger(128, random)).toString();
				Date expirationDate = new Date((new Date()).getTime() + maxAge * 1000L);

				CriteriaBuilder builder = entityManager.getCriteriaBuilder();
				CriteriaQuery<ExpiringRollingToken> criteriaQuery = builder.createQuery(ExpiringRollingToken.class);
				Root<ExpiringRollingToken> from = criteriaQuery.from(ExpiringRollingToken.class);
				Object principal = RollingToken.getConfiguredPrincipal(realmName, principalType, info.getPrincipals());
				Predicate predicate = builder.and(builder.equal(from.get("name"), principal.toString()),
					builder.equal(from.get("hostAddress"), request.getRemoteAddr()));
				ExpiringRollingToken expiringRollingToken = null;
				List<ExpiringRollingToken> results = entityManager.createQuery(criteriaQuery.where(predicate)).getResultList();

				if (results.size() <= 0) expiringRollingToken = new ExpiringRollingToken(principal.toString(), tokenValue,
					request.getRemoteAddr(), expirationDate);
				else {
					expiringRollingToken = results.remove(0);
					// there shouldn't be any remaining, but remove the rest in case there are
					for (ExpiringRollingToken obsoleteToken : results)
						entityManager.remove(obsoleteToken);
					expiringRollingToken.setExpiresAfter(expirationDate);
					expiringRollingToken.setValue(tokenValue);
				}
				entityManager.persist(expiringRollingToken);
				cookies.writeCookieValue(RollingToken.TOKEN_NAME, tokenValue);

			}
		}

		// TODO Should the argument be Collection<Object> principals instead?
		protected void eraseAllExpiringRollingTokens(Collection<RollingToken> rollingTokens) {
			// List<ExpiringRollingToken> rollingTokens = session.createCriteria(ExpiringRollingToken.class)
			// .add(Restrictions.eq("name", principal.toString())).list();
			// for (ExpiringRollingToken token : rollingTokens)
			// session.delete(token);
			Query query = entityManager.createQuery("delete from " + ExpiringRollingToken.class.getSimpleName()
				+ " where name = ?1");
			for (RollingToken token : rollingTokens) {
				query.setParameter(1, token.getPrincipal().toString());
				query.executeUpdate();
			}
		}

	}

}
