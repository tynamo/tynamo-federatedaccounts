package org.tynamo.security.federatedaccounts.pac4j.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.pac4j.core.exception.CommunicationException;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.base.AbstractOauthSignIn;
import org.tynamo.security.federatedaccounts.pac4j.pages.Pac4jOauth;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public class Pac4jOauthSignIn extends AbstractOauthSignIn {
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "800")
	@Property
	private Integer width;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "400")
	@Property
	private Integer height;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private WindowMode windowMode;

	@Inject
	private AssetSource assetSource;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	@Property(write = false)
	private String provider;

	public Asset getSignInImage() {
		return assetSource.getAsset(componentResources.getBaseResource(), provider + ".png", null);
	}

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	@Inject
	private PageRenderLinkSource linkSource;

	@Override
	protected Class<?> getOauthPageClass() {
		return Pac4jOauth.class;
	}

	public String getOauthAuthorizationLink() throws CommunicationException {
		return getOauthRedirectLink(windowMode, provider, "request_token", getReturnPageUri());
	}

	public String getProviderPrefix() {
		return FederatedAccountType.pac4j_ + provider;
	}

}
