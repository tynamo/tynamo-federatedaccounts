package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;

@Import(library = "OauthSignIn.js")
public abstract class AbstractOauthSignIn extends OauthComponentBase {
	// Use ^ for current page the component is contained in, empty for configured success url
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "prop:defaultReturnPage")
	String returnPageName;

	public String getReturnPageName() {
		return returnPageName;
	}

	@Inject
	@Symbol(FederatedAccountSymbols.DEFAULT_RETURNPAGE)
	@Property(write = false)
	String defaultReturnPage;

	@Inject
	PageRenderLinkSource pageRenderLinkSource;

	@Inject
	ComponentResources componentResources;

	@Inject
	BaseURLSource baseURLSource;

	@Inject
	Request request;

	public String getReturnPageUri() {
		if ("^".equals(returnPageName))
			return pageRenderLinkSource.createPageRenderLink(componentResources.getPage().getClass()).toAbsoluteURI();

		// Empty returnpageName denotes a base url. Default for default return page may be set to a non empty value.
		// If default is empty and not locally overridden, return the baseurl
		return "".equals(returnPageName) && "".equals(defaultReturnPage) ? baseURLSource.getBaseURL(request.isSecure())
			: pageRenderLinkSource.createPageRenderLink(returnPageName).toAbsoluteURI();

	}
}
