package org.tynamo.security.federatedaccounts.services;

import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.BeanBlockSource;

/**
 * A contribution to the {@link BeanBlockSource} service, defining a page name and block id (within the page) that can edit or display a
 * particular type of property.
 */
public class FederatedSignInComponentContribution {
	private final String key;

	private final String pageName;

	public FederatedSignInComponentContribution(String key, String pageName) {
		assert InternalUtils.isNonBlank(key);
		assert InternalUtils.isNonBlank(pageName);
		this.key = key;
		this.pageName = pageName;
	}

	/**
	 * The type of data for which the indicated block will provide an editor or displayer for.
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * The logical name of the page containing the block.
	 */
	public final String getPageName() {
		return pageName;
	}

}
