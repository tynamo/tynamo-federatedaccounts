package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public abstract class OauthComponentBase {
	private static final String CLIENTID = ".clientid";
	private static final String CLIENTSECRET = ".clientsecret";
	@Inject
	@Symbol(FederatedAccountSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	@Inject
	@Symbol(FederatedAccountSymbols.DEFAULT_REMEMBERME)
	private boolean rememberMe;

	@Inject
	protected Logger logger;

	public boolean isAutocommit() {
		return autocommit;
	}

	@Inject
	private SymbolSource symbolSource;

	public String getProviderPrefix() {
		String name = getClass().getSimpleName();
		if (name.startsWith("OpenId")) return FederatedAccountType.openid.name();
		try {
			return FederatedAccountType.valueOf(name.substring(0, name.indexOf("Oauth")).toLowerCase()).name();
			// FIXME implement the try-catch properly
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getOauthClientId() {
		return symbolSource.valueForSymbol(getProviderPrefix() + CLIENTID);
	}

	protected String getOauthClientSecret() {
		return symbolSource.valueForSymbol(getProviderPrefix() + CLIENTSECRET);
	}

	public boolean isOauthConfigured() {
		return !"".equals(getOauthClientId()) && !"".equals(getOauthClientSecret());
	}

	protected abstract Class getOauthPageClass();

	@Inject
	private PageRenderLinkSource linkSource;

	protected String getOauthRedirectLink(Object... context) {
		if (context == null || !(context[0] instanceof WindowMode))
			throw new IllegalArgumentException("WindowMode is required as the first context parameter");
		return linkSource.createPageRenderLinkWithContext(getOauthPageClass(), context).toAbsoluteURI();
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
