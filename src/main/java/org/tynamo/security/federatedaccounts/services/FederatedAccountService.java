package org.tynamo.security.federatedaccounts.services;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

public interface FederatedAccountService {
	public static final String IDPROPERTY = ".id";

	AuthenticationInfo federate(String realmName, Object remotePrincipal, AuthenticationToken authenticationToken, Object remoteAccount);
}
