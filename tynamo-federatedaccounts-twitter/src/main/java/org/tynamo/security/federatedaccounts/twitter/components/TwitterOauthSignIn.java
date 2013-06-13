package org.tynamo.security.federatedaccounts.twitter.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.base.AbstractOauthSignIn;
import org.tynamo.security.federatedaccounts.twitter.pages.TwitterOauth;
import org.tynamo.security.federatedaccounts.util.WindowMode;

import twitter4j.TwitterException;

public class TwitterOauthSignIn extends AbstractOauthSignIn {
	// from https://dev.twitter.com/docs/auth/sign-in-with-twitter
	public enum ButtonStyle {
		darker("sign-in-with-twitter-d.png"), darker_small("sign-in-with-twitter-d-sm.png"), lighter(
			"sign-in-with-twitter-l.png"), ligter_small("sign-in-with-twitter-l-sm.png");
		private String resourceName;

		ButtonStyle(String resourceName) {
			this.resourceName = resourceName;
		}

		@Override
		public String toString() {
			return resourceName;
		}
	}

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "800")
	@Property
	private Integer width;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "400")
	@Property
	private Integer height;

	@Parameter(value = "darker", required = false, defaultPrefix = "literal")
	private ButtonStyle buttonStyle;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private WindowMode windowMode;

	@Inject
	private AssetSource assetSource;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	public Asset getSignInImage() {
		return assetSource.getAsset(componentResources.getBaseResource(), buttonStyle.toString(), null);
	}

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	@Inject
	private PageRenderLinkSource linkSource;

	@Override
	protected Class<?> getOauthPageClass() {
		return TwitterOauth.class;
	}

	public String getOauthAuthorizationLink() throws TwitterException {
		return getOauthRedirectLink(windowMode, "request_token", getReturnPageUri());
	}
}
