package org.tynamo.security.federatedaccounts.services;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(FederatedSignInComponentContribution.class)
public interface FederatedSignInComponentBlockSource {
	/**
	 * Returns a block which can be used to render an editor for the given data type, in the form of a field label and input field.
	 * 
	 * @param datatype
	 *          logical name for the type of data to be displayed
	 * @return the Block
	 * @throws RuntimeException
	 *           if no appropriate block is available
	 */
	Block toBlock(String key);

}
