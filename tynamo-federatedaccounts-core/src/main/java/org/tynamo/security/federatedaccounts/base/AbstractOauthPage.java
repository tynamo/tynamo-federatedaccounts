package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.tynamo.security.federatedaccounts.util.WindowMode;

/**
 * 
 * The purpose of the class is to serve as a placeholder for @CommitAfter annotation and invoke it conditionally
 * 
 */
public abstract class AbstractOauthPage extends OauthComponentBase {

	private WindowMode windowMode;

	public WindowMode getWindowMode() {
		return windowMode;
	}

	protected final Object onActivate(EventContext context) throws Exception {
		if (context.getCount() <= 0)
			throw new IllegalArgumentException("Explicit windowMode is required but was not specified as a context argument");
		windowMode = WindowMode.valueOf(context.get(String.class, 0));

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

	@Override
	protected Class getOauthPageClass() {
		return getClass();
	}

}
