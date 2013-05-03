package org.tynamo.security.federatedaccounts.twitter.services;

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
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.federatedaccounts.services.FederatedSignInComponentBlockSource;
import org.tynamo.security.federatedaccounts.services.FederatedSignInComponentContribution;
import org.tynamo.security.federatedaccounts.twitter.pages.TwitterOauth;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

import twitter4j.TwitterFactory;

public class TwitterFederatedAccountsModule {
	private static final String PATH_PREFIX = "federated-twitter";
	// this is a child module, use the same version as for the parent
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, TwitterRealm.class).withId(TwitterRealm.class.getSimpleName());
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(TwitterRealm.TWITTER_PRINCIPAL, TwitterRealm.PrincipalProperty.id.name());
		configuration.add(TwitterRealm.TWITTER_CLIENTID, "");
		configuration.add(TwitterRealm.TWITTER_CLIENTSECRET, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(FederatedAccountsModule.PATH_PREFIX,
			"org.tynamo.security.federatedaccounts.twitter"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts/twitter");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
		@InjectService("TwitterRealm") AuthenticatingRealm twitterRealm) {
		configuration.add(twitterRealm);
	}

	public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> configuration,
		SecurityFilterChainFactory factory) {
		configuration.add(factory
			.createChain("/" + FederatedAccountsModule.PATH_PREFIX + "/" + TwitterOauth.class.getSimpleName().toLowerCase())
			.add(factory.anon()).build());
	}

	public static TwitterFactory build() {
		return new TwitterFactory();
	}

	@Contribute(FederatedSignInComponentBlockSource.class)
	public static void addSignInComponentBlocks(Configuration<FederatedSignInComponentContribution> configuration) {
		configuration.add(new FederatedSignInComponentContribution(FederatedAccountType.twitter.name(),
			"federated/twitterSignInComponentBlocks"));
	}

}