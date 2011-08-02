package org.tynamo.security.federatedaccounts.scribe;

import org.apache.tapestry5.services.PageRenderLinkSource;
import org.scribe.model.OAuthConfig;
import org.tynamo.security.federatedaccounts.scribe.pages.Callback;

public class TynamoOAuthConfig extends OAuthConfig {
	private PageRenderLinkSource linkSource;
	private String api;

	public TynamoOAuthConfig(String apiName, String key, String secret, String scope, final PageRenderLinkSource linkSource) {
		super(key, secret, null, null, scope);
		this.linkSource = linkSource;
		this.api = apiName;
	}

	@Override
	public String getCallback() {
		return linkSource.createPageRenderLinkWithContext(Callback.class, api).toAbsoluteURI();
	}
}
