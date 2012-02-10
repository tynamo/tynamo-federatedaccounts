package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;

public class OauthComponentBase {
	private static final String CLIENTID = ".clientid";
	private static final String CLIENTSECRET = ".clientsecret";
	@Inject
	@Symbol(FederatedAccountSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	public boolean isAutocommit() {
		return autocommit;
	}

	@Inject
	private SymbolSource symbolSource;

	public FederatedAccountType getAccountType() {
		String name = getClass().getSimpleName();
		try {
			return FederatedAccountType.valueOf(name.substring(0, name.indexOf("Oauth")).toLowerCase());
			// FIXME implement the try-catch properly
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getOauthClientId() {
		return symbolSource.valueForSymbol(getAccountType().name() + CLIENTID);
	}

	protected String getOauthClientSecret() {
		return symbolSource.valueForSymbol(getAccountType().name() + CLIENTSECRET);
	}

	public boolean isOauthConfigured() {
		return !"".equals(getOauthClientId()) && !"".equals(getOauthClientSecret());
	}

	@Inject
	private PageRenderLinkSource linkSource;
}
