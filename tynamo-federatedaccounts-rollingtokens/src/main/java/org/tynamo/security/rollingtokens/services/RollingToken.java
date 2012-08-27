package org.tynamo.security.rollingtokens.services;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;

public class RollingToken implements AuthenticationToken, HostAuthenticationToken, RememberMeAuthenticationToken {
	public static final String TOKEN_NAME = "rollingToken";

	private Object principal;
	private String rollingToken;
	private String hostAddress;

	public RollingToken(Object principal, String rollingToken, String remoteAddress) {
		this.principal = principal;
		this.rollingToken = rollingToken;
		hostAddress = remoteAddress;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3054718345404435729L;

	public Object getCredentials() {
		return rollingToken;
	}

	public Object getPrincipal() {
		return principal;
	}

	public boolean isRememberMe() {
		// Always return true, otherwise rememberMe token is erased when this authentication is used
		return true;
	}

	public String getHost() {
		return hostAddress;
	}

	// TODO the implementation is almost the same as in tapestry-security-jpa's SecureEntityManager.getConfiguredPrincipal
	// is there a possibility to refactor this to some common class?
	public static Object getConfiguredPrincipal(String realmName, Class<?> principalType,
		PrincipalCollection allPrincipals) {
		if (!realmName.isEmpty()) {
			Collection<?> principals = allPrincipals.fromRealm(realmName);
			if (principalType == null) principals.iterator().next();
			else {
				for (Object availablePrincipal : principals)
					if (availablePrincipal.getClass().isAssignableFrom(principalType)) { return availablePrincipal; }
			}
		}
		if (principalType != null) {
			Object principal = allPrincipals.oneByType(principalType);
			if (principal == null)
				throw new NullPointerException("Subject is required to have a configured principal of type '" + principalType);
			return principal;
		}
		return allPrincipals.getPrimaryPrincipal();
	}

}
