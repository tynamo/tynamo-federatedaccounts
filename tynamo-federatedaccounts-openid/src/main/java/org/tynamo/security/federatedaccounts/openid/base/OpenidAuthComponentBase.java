package org.tynamo.security.federatedaccounts.openid.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.URLEncoder;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.openid.pages.CommitOpenidAuth;
import org.tynamo.security.federatedaccounts.openid.pages.OpenIdAuth;

public class OpenidAuthComponentBase {
	@Inject
	@Symbol(FederatedAccountSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	public boolean getAutocommit() {
		return autocommit;
	}

	@Inject
	private URLEncoder urlEncoder;

	@Inject
	private PageRenderLinkSource linkSource;

	// Final since signin and oauth *must* share the same implementation (or at least use the same link)
	protected final String getAuthRedirectLink(String redirectUrl) {
		String result = null;
		if (redirectUrl != null) {
			result = linkSource.createPageRenderLinkWithContext(autocommit ? CommitOpenidAuth.class : OpenIdAuth.class,
				urlEncoder.encode(redirectUrl)).toAbsoluteURI();
		} else {
			result = linkSource.createPageRenderLink(autocommit ? CommitOpenidAuth.class : OpenIdAuth.class).toAbsoluteURI();
		}

		return result;
	}
}
