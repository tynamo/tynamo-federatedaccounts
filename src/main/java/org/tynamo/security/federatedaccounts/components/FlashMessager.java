package org.tynamo.security.federatedaccounts.components;

import org.apache.tapestry5.annotations.Persist;

public class FlashMessager {
	@Persist("flash")
	private String successMessage;

	@Persist("flash")
	private String failureMessage;

	public String getSuccessMessage() {
		return successMessage;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.failureMessage = null;
		this.successMessage = successMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.successMessage = null;
		this.failureMessage = failureMessage;

	}
}
