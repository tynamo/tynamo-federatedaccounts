package org.tynamo.security.federatedaccounts.services;

import java.util.Set;

import org.apache.tapestry5.Block;

public interface FederatedSignInOptions {
	public enum OptionType {
		primary, secondary, disabled
	}

	public Set<Block> getPrimaryBlocks();

	public Set<Block> getSecondaryBlocks();
}
