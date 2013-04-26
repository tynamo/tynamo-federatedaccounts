package org.tynamo.security.federatedaccounts.testapp.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;

public class Index {

	@Inject
	private SecurityService securityService;

	public String getStatus() {
		return securityService.isAuthenticated() ? "Authenticated" : "Not Authenticated";
	}
}
