package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.federatedaccounts.services.FederatedSignInOptions;

public class FederatedSignInOptionsPanel {
	@Inject
	@Property(write = false)
	private FederatedSignInOptions options;

	@Property
	private Block signInBlock;

	@InjectComponent
	CollapsiblePanel collapsiblePanel;

	@SetupRender
	void setupRender() {
		collapsiblePanel.setExpanderDisabled(options.getSecondaryBlocks().size() <= 0);
	}
}
