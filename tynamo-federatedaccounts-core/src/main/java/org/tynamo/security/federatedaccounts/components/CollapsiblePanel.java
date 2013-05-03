package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "CollapsiblePanel.js", stylesheet = "CollapsiblePanel.css")
public class CollapsiblePanel {

	@Inject
	private JavaScriptSupport jss;

	@Parameter(value = "defaultCollapsed", defaultPrefix = "literal")
	private boolean collapsed;

	@Parameter(defaultPrefix = "literal")
	@Property
	private String title;

	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	@Property(write = false)
	private Block headerContent;

	@Parameter(defaultPrefix = "literal")
	private boolean expanderDisabled;

	public boolean isDefaultCollapsed() {
		return true;
	}

	public String getState() {
		return collapsed ? "collapsed" : "expanded";
	}

	final void afterRender(MarkupWriter writer) {
		// writer.end(); // input

		// jss.addScript("$('#%s').spectrum(%s);", getClientId(), spec);
	}

	public boolean isExpanderDisabled() {
		return expanderDisabled;
	}

	public void setExpanderDisabled(boolean expanderDisabled) {
		this.expanderDisabled = expanderDisabled;
	}

}
