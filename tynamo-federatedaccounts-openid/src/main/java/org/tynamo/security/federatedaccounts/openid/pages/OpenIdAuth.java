package org.tynamo.security.federatedaccounts.openid.pages;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tapestry5.annotations.Log;
import org.tynamo.security.federatedaccounts.openid.base.AbstractOpenidAuthPage;

public class OpenIdAuth extends AbstractOpenidAuthPage {

//	@Override
//	@Log
//	protected void onActivate(String windowMode) throws MalformedURLException {
//		System.out.println("**************************** OPEN ID OAUTH");
//		super.onActivate(windowMode);
//	}
	
	@Log
	@Override
	protected URL onActivate(String returningPage) throws MalformedURLException {
		return super.onActivate(returningPage);
	}
	
}
