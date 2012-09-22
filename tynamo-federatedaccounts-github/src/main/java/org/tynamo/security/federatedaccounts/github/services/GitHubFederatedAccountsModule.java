package org.tynamo.security.federatedaccounts.github.services;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.github.pages.GitHubOauth;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

public class GitHubFederatedAccountsModule {
	private static final String PATH_PREFIX = "github";
	// this is a child module, use the same version as for the parent
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, GitHubRealm.class).withId(GitHubRealm.class.getSimpleName());
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(GitHubRealm.GITHUB_PRINCIPAL, GitHubRealm.PrincipalProperty.login.name());
		configuration.add(GitHubRealm.GITHUB_CLIENTID, "");
		configuration.add(GitHubRealm.GITHUB_CLIENTSECRET, "");
		configuration.add(GitHubRealm.GITHUB_SCOPE, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts.github"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts/github");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
		@InjectService("GithubRealm") AuthenticatingRealm githubRealm) {
		configuration.add(githubRealm);
	}

	public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> configuration,
		SecurityFilterChainFactory factory) {
		configuration.add(factory.createChain("/" + PATH_PREFIX + "/" + GitHubOauth.class.getSimpleName().toLowerCase())
			.add(factory.anon()).build());
	}

}
