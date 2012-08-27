package org.tynamo.security.federatedaccounts.openid.services;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.openid.pages.OpenIdAuth;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

public class OpenidFederatedAccountsModule {
	private static final String PATH_PREFIX = "openid";

	private static String version = ModuleProperties.getVersion(OpenidFederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, OpenidRealm.class).withId(OpenidRealm.class.getSimpleName());
		binder.bind(OpenidLoginManager.class, OpenidLoginManagerImpl.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		// configuration.add(OpenidRealm.FACEBOOK_PRINCIPAL, OpenidRealm.PrincipalProperty.id.name());
		// configuration.add(OpenidRealm.FACEBOOK_PERMISSIONS, "");
		// configuration.add(OpenidRealm.FACEBOOK_CLIENTID, "");
		// configuration.add(OpenidRealm.FACEBOOK_CLIENTSECRET, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts.openid"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts/openid");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
		@InjectService("OpenidRealm") AuthenticatingRealm openidRealm) {
		configuration.add(openidRealm);
	}

	public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> configuration,
		SecurityFilterChainFactory factory) {
		configuration.add(factory.createChain("/" + PATH_PREFIX + "/" + OpenIdAuth.class.getSimpleName().toLowerCase())
			.add(factory.anon()).build());
	}
}
