package org.tynamo.security.federatedaccounts.openid.pages;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.openid.base.AbstractOpenidAuthPage;

public class CommitOpenidAuth extends AbstractOpenidAuthPage {

//	@CommitAfter
//	@Override
//	protected onActivate(String windowMode) throws MalformedURLException {
//		System.out.println("**************************** COMMIT OPEN ID OAUTH");
//		super.onActivate(windowMode);
//		return true;
//	}
	
	@Log
	@CommitAfter
	@Override
	protected URL onActivate(String returningPage) throws MalformedURLException {
		return super.onActivate(returningPage);
	}
	
}
