package org.tynamo.security.federatedaccounts.github.services;

import java.io.IOException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

public class GitHubRealm extends AuthenticatingRealm {

	public static final String GITHUB_PRINCIPAL = "github.principal";
	public static final String GITHUB_CLIENTID = "github.clientid";
	public static final String GITHUB_CLIENTSECRET = "github.clientsecret";
	public static final String GITHUB_SCOPE = "github.scope";

	public static enum PrincipalProperty {
		login, email, id
	}

	private FederatedAccountService federatedAccountService;
	private Logger logger;
	private PrincipalProperty principalProperty;

	public GitHubRealm(Logger logger, FederatedAccountService federatedAccountService,
		@Inject @Symbol(GITHUB_PRINCIPAL) String principalPropertyName) {
		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;
		this.principalProperty = PrincipalProperty.valueOf(principalPropertyName);
		setName(FederatedAccount.FederatedAccountType.github.name());
		setAuthenticationTokenClass(GitHubAccessToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
		throws AuthenticationException {

		GitHubClient client = new GitHubClient().setOAuth2Token(authenticationToken.getCredentials().toString());

		UserService userService = new UserService(client);

		User githubUser;
		try {
			githubUser = userService.getUser();
		} catch (IOException e) {
			logger.error("Error getting GitHub user", e);
			throw new AuthenticationException(e);
		}

		String principalValue;
		switch (principalProperty) {
		case email:
			principalValue = githubUser.getEmail();
			break;
		case id:
			principalValue = githubUser.getId() + "";
			break;
		default:
			principalValue = githubUser.getLogin();
			break;
		}

		AuthenticationInfo authenticationInfo = federatedAccountService.federate(
			FederatedAccount.FederatedAccountType.github.name(), principalValue, authenticationToken, githubUser);

		return authenticationInfo;
	}
}
