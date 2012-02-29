package org.tynamo.security.rollingtokens.services;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

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

}
