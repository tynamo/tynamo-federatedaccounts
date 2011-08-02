package org.tynamo.security.federatedaccounts.scribe.google;

import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.scribe.TynamoOAuthConfig;

public class GoogleOAuth20Config extends TynamoOAuthConfig {

	private final static String defaultScope = "https://www.google.com/m8/feeds/";

	public GoogleOAuth20Config(String apiName, String key, String secret, PageRenderLinkSource linkSource) {
		super(apiName, key, secret, defaultScope, linkSource);
	}

	public GoogleOAuth20Config(String apiName, String key, String secret, String scope, PageRenderLinkSource linkSource) {
		super(apiName, key, secret, new StringBuilder(defaultScope).append(" ").append(scope).toString(), linkSource);
	}
}
