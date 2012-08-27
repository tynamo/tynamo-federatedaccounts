package org.tynamo.security.federatedaccounts.openid.components;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.URLEncoder;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.base.OauthComponentBase;
import org.tynamo.security.federatedaccounts.openid.Provider;
import org.tynamo.security.federatedaccounts.openid.pages.OpenIdAuth;
import org.tynamo.security.federatedaccounts.openid.services.OpenidLoginManager;
import org.tynamo.security.federatedaccounts.util.WindowMode;

@Import(stylesheet = "button.css")
public class OpenidSignIn extends OauthComponentBase {

	@Parameter(required = false, defaultPrefix = "literal")
	private Provider provider;

	@Inject
	private Logger logger;

	@Inject
	private OpenidLoginManager loginManager;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private RequestGlobals requestGlobals;

	@Property
	private String currentUrl;

	@SetupRender
	public void componentStartup() {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		StringBuilder sb = new StringBuilder();
		sb.append(request.getRequestURL());
		if (StringUtils.isNotBlank(request.getQueryString())) {
			sb.append("?");
			sb.append(request.getQueryString());
		}
		currentUrl = sb.toString();

		// logger.info("Activating OpenIdSignIn component: " + currentUrl);
	}

	@Inject
	private URLEncoder urlEncoder;

	public void onAction(String currentPage) {
		logger.info("Clicked link: " + provider);
		logger.info(" ******** current url: " + currentPage);

		// loginManager.requestAuthentication(provider, getAuthRedirectLink(currentPage));
		loginManager
			.requestAuthentication(provider, getOauthRedirectLink(WindowMode.blank, urlEncoder.encode(currentPage)));
	}

	public boolean getIsProviderNajdi() {
		if (provider != null) { return provider.equals(Provider.najdi); }
		return false;
	}

	public boolean getIsProviderGoogle() {
		if (provider != null) { return provider.equals(Provider.google); }
		return false;
	}

	public boolean getIsProviderGeneral() {
		return !getIsProviderGoogle() && !getIsProviderNajdi();
	}

	@Override
	protected Class getOauthPageClass() {
		return OpenIdAuth.class;
	}

}
