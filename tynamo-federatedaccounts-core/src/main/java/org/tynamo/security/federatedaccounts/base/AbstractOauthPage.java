package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;

public abstract class AbstractOauthPage extends OauthComponentBase {

	protected final Object onActivate(EventContext context) throws Exception {
		return isAutocommit() ? commitAfterOnActivate(context) : onOauthActivate(context);
	}

	@CommitAfter
	protected Object commitAfterOnActivate(EventContext context) throws Exception {
		return onOauthActivate(context);
	}

	protected abstract Object onOauthActivate(EventContext context) throws Exception;

	public String getTitle() {
		return getAccountType().name() + " account";
	}

}
