package org.tynamo.security.federatedaccounts.facebook.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.base.AbstractOauthSignIn;
import org.tynamo.security.federatedaccounts.facebook.pages.FacebookOauth;
import org.tynamo.security.federatedaccounts.facebook.services.FacebookRealm;
import org.tynamo.security.federatedaccounts.util.WindowMode;

@Import(stylesheet = "fb-button.css")
public class FacebookOauthSignIn extends AbstractOauthSignIn {

	@Inject
	@Symbol(FacebookRealm.FACEBOOK_PERMISSIONS)
	@Property
	private String facebookPermissions;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "800")
	@Property
	private Integer width;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "400")
	@Property
	private Integer height;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "blank")
	private WindowMode windowMode;

	@Inject
	private AssetSource assetSource;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	public String getOauthAuthorizationLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://graph.facebook.com/oauth/authorize?client_id=");
		sb.append(getOauthClientId());
		sb.append("&redirect_uri=");
		sb.append(getOauthRedirectLink());
		sb.append("&display=popup");
		sb.append("&scope=");
		sb.append(facebookPermissions);

		return sb.toString();
	}

	public String getOauthRedirectLink() {
		if ("".equals(getReturnPageName())) return getOauthRedirectLink(windowMode);
		if ("^".equals(getReturnPageName()))
			return getOauthRedirectLink(windowMode,
				pageRenderLinkSource.createPageRenderLink(componentResources.getPage().getClass()).toAbsoluteURI());
		return getOauthRedirectLink(windowMode, pageRenderLinkSource.createPageRenderLink(getReturnPageName())
			.toAbsoluteURI());
	}

	@Override
	protected Class<?> getOauthPageClass() {
		return FacebookOauth.class;
	}


}
