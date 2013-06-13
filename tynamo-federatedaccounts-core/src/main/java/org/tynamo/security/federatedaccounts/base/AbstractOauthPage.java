package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.tynamo.security.federatedaccounts.util.WindowMode;

/**
 * 
 * The purpose of the class is to serve as a placeholder for @CommitAfter annotation and invoke it conditionally
 * 
 */
public abstract class AbstractOauthPage extends OauthComponentBase {

	private WindowMode windowMode;

	private String returnUri;

	private boolean oauthAuthenticated;

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
		return getProviderPrefix() + " account";
	}

	@Override
	protected Class getOauthPageClass() {
		return getClass();
	}

	public String getReturnUri() {
		return returnUri;
	}

	public void setReturnUri(String returnUri) {
		this.returnUri = returnUri;
	}

	public boolean isOauthAuthenticated() {
		return oauthAuthenticated;
	}

	public void setOauthAuthenticated(boolean oauthAuthenticated) {
		this.oauthAuthenticated = oauthAuthenticated;
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (isOauthAuthenticated())
			javaScriptSupport.addScript("onAuthenticationSuccess('" + getReturnUri() + "', '" + getWindowMode().name()
				+ "');");
	}
}
