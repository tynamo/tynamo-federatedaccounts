package org.tynamo.security.federatedaccounts.services;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.pages.CommitFacebookOauth;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;
import org.tynamo.security.federatedaccounts.realms.FacebookRealm;
import org.tynamo.security.federatedaccounts.util.WindowMode;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

public class FederatedAccountsModule {
	private static final String PATH_PREFIX = "federated";
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, FacebookRealm.class).withId(FacebookRealm.class.getSimpleName());
		binder.bind(OAuthServicetLocator.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(FederatedAccountSymbols.COMMITAFTER_OAUTH, "true");
		configuration.add(FederatedAccountSymbols.HTTPCLIENT_ON_GAE, "false");
		configuration.add(FederatedAccountSymbols.SUCCESSURL, "");
		configuration.add(FacebookRealm.FACEBOOK_PRINCIPAL, FacebookRealm.PrincipalProperty.id.name());
		configuration.add(FacebookRealm.FACEBOOK_PERMISSIONS, "");
		configuration.add(FacebookRealm.FACEBOOK_CLIENTID, "");
		configuration.add(FacebookRealm.FACEBOOK_CLIENTSECRET, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
			@InjectService("FacebookRealm") AuthenticatingRealm facebookRealm) {
		configuration.add(facebookRealm);
	}

	public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> configuration,
			SecurityFilterChainFactory factory) {
		configuration.add(factory.createChain("/" + PATH_PREFIX + "/"
				+ FacebookOauth.class.getSimpleName().toLowerCase()).add(factory.anon()).build());
		configuration.add(factory.createChain("/" + PATH_PREFIX + "/"
				+ CommitFacebookOauth.class.getSimpleName().toLowerCase()).add(factory.anon()).build());
	}	
	
	public void contributeTypeCoercer(Configuration<CoercionTuple<String, WindowMode>> configuration) {
		configuration.add(new CoercionTuple<String, WindowMode>(String.class, WindowMode.class, new Coercion<String, WindowMode>() {
			public WindowMode coerce(String input) {
				return WindowMode.valueOf(input);
			}
		}));
	}

}
