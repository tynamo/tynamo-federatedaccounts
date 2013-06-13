package org.tynamo.security.federatedaccounts.services;

import java.util.Collection;
import java.util.Map;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.slf4j.Logger;

public class FederatedSignInComponentBlockSourceImpl implements FederatedSignInComponentBlockSource {
	private final Map<String, FederatedSignInComponentContribution> signInComponentMap = CollectionFactory
		.newCaseInsensitiveMap();

	private Logger logger;

	private RequestPageCache pageCache;

	public FederatedSignInComponentBlockSourceImpl(Logger logger, RequestPageCache pageCache,
		Collection<FederatedSignInComponentContribution> configuration) {
		this.logger = logger;
		this.pageCache = pageCache;
		for (FederatedSignInComponentContribution contribution : configuration)
			signInComponentMap.put(contribution.getKey(), contribution);
	}

	@Override
	public Block toBlock(String key) {
		FederatedSignInComponentContribution contribution = signInComponentMap.get(key);
		if (contribution == null) return null;

		Page page = pageCache.get(contribution.getPageName());

		return page.getRootElement().getBlock(contribution.getKey() + "_small"); // contribution.getBlockId());
	}

}
