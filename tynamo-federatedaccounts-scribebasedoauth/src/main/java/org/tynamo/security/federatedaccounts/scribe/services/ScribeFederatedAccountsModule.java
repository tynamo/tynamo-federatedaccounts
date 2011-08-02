package org.tynamo.security.federatedaccounts.scribe.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;

public class ScribeFederatedAccountsModule {
	private static final String PATH_PREFIX = "federated";
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(OAuthServiceLocator.class);
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts");
	}

}
