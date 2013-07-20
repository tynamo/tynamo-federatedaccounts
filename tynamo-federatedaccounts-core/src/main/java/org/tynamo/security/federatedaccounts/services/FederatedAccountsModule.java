package org.tynamo.security.federatedaccounts.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public class FederatedAccountsModule {
	public static final String PATH_PREFIX = "federated";
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(FederatedSignInComponentBlockSource.class, FederatedSignInComponentBlockSourceImpl.class);
		binder.bind(FederatedSignInOptions.class, FederatedSignInOptionsImpl.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(FederatedAccountSymbols.COMMITAFTER_OAUTH, "true");
		configuration.add(FederatedAccountSymbols.HTTPCLIENT_ON_GAE, "false");
		configuration.add(FederatedAccountSymbols.DEFAULT_RETURNPAGE, "");
		configuration.add(FederatedAccountSymbols.DEFAULT_REMEMBERME, "true");
		configuration.add(FederatedAccountSymbols.LOCALACCOUNT_REALMNAME, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts");
	}

	public void contributeTypeCoercer(Configuration<CoercionTuple<String, WindowMode>> configuration) {
		configuration.add(new CoercionTuple<String, WindowMode>(String.class, WindowMode.class,
			new Coercion<String, WindowMode>() {
				public WindowMode coerce(String input) {
					return WindowMode.valueOf(input);
				}
			}));
	}

}
