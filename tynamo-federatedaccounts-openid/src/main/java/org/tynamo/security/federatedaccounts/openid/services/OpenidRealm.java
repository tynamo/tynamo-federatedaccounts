package org.tynamo.security.federatedaccounts.openid.services;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.tapestry5.annotations.Log;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.openid.OpenidAccessToken;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

/**
 * <p>
 * A {@link org.apache.shiro.realm.Realm} that authenticates using OpenID.
 */
public class OpenidRealm extends AuthenticatingRealm {

	private Logger logger;

	public static enum PrincipalProperty {
		id, email, name
	}

	private PrincipalProperty principalProperty;

	private FederatedAccountService federatedAccountService;

	public OpenidRealm(Logger logger, FederatedAccountService federatedAccountService) {
		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;

		setName(FederatedAccount.FederatedAccountType.openid.name());
		setAuthenticationTokenClass(OpenidAccessToken.class);
	}

	@Override
	@Log
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
		throws AuthenticationException {
		OpenidAccessToken token = (OpenidAccessToken) authenticationToken;

		return federatedAccountService.federate(FederatedAccount.FederatedAccountType.openid.name(), token.getPrincipal(),
			authenticationToken, token.getVerificationResult());
	}

}
