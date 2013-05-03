package org.tynamo.security.federatedaccounts.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.Block;

public class FederatedSignInOptionsImpl implements FederatedSignInOptions {
	private Set<Block> primaryBlocks;
	private Set<Block> secondaryBlocks;

	public FederatedSignInOptionsImpl(FederatedSignInComponentBlockSource componentBlockSource,
		Map<String, OptionType> configuration) {
		primaryBlocks = new HashSet<Block>();
		secondaryBlocks = new HashSet<Block>();

		for (Map.Entry<String, OptionType> entry : configuration.entrySet()) {
			if (OptionType.primary.equals(entry.getValue())) primaryBlocks.add(componentBlockSource.toBlock(entry.getKey()));
			else if (OptionType.secondary.equals(entry.getValue()))
				secondaryBlocks.add(componentBlockSource.toBlock(entry.getKey()));
		}

	}

	public Set<Block> getPrimaryBlocks() {
		return primaryBlocks;
	}

	public Set<Block> getSecondaryBlocks() {
		return secondaryBlocks;
	}
}
