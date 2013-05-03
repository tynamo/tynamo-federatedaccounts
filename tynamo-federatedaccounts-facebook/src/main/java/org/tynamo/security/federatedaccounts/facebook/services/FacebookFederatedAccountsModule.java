package org.tynamo.security.federatedaccounts.facebook.services;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.facebook.pages.FacebookOauth;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.federatedaccounts.services.FederatedSignInComponentBlockSource;
import org.tynamo.security.federatedaccounts.services.FederatedSignInComponentContribution;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

public class FacebookFederatedAccountsModule {
	private static final String PATH_PREFIX = "federated-facebook";
	private static String version = ModuleProperties.getVersion(FacebookFederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, FacebookRealm.class).withId(FacebookRealm.class.getSimpleName());
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(FacebookRealm.FACEBOOK_PRINCIPAL, FacebookRealm.PrincipalProperty.id.name());
		configuration.add(FacebookRealm.FACEBOOK_PERMISSIONS, "");
		configuration.add(FacebookRealm.FACEBOOK_CLIENTID, "");
		configuration.add(FacebookRealm.FACEBOOK_CLIENTSECRET, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(FederatedAccountsModule.PATH_PREFIX,
			"org.tynamo.security.federatedaccounts.facebook"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts/facebook");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
			@InjectService("FacebookRealm") AuthenticatingRealm facebookRealm) {
		configuration.add(facebookRealm);
	}

	public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> configuration,
			SecurityFilterChainFactory factory) {
		configuration.add(factory
			.createChain("/" + FederatedAccountsModule.PATH_PREFIX + "/"
				+ FacebookOauth.class.getSimpleName().toLowerCase()).add(factory.anon()).build());
	}

	@Contribute(FederatedSignInComponentBlockSource.class)
	public static void addSignInComponentBlocks(Configuration<FederatedSignInComponentContribution> configuration) {
		configuration.add(new FederatedSignInComponentContribution(FederatedAccountType.facebook.name(),
			"federated/facebookSignInComponentBlocks"));
	}

}
