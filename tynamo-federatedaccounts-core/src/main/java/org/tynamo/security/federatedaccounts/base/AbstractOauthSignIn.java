package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;

@Import(library = "OauthSignIn.js")
public abstract class AbstractOauthSignIn extends OauthComponentBase {
	@Inject
	private ComponentClassResolver resolver;

	// Use ^ for current page the component is contained in, empty for configured success url
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "prop:defaultReturnPage")
	private String returnPageName;

	public String getReturnPageName() {
		return returnPageName;
	}

	@Inject
	@Symbol(FederatedAccountSymbols.DEFAULT_RETURNPAGE)
	private String defaultReturnPage;

	public String getDefaultReturnPage() {
		return defaultReturnPage;
	}

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Inject
	private ComponentResources componentResources;

	public String getReturnPageUri() {
		if ("".equals(returnPageName)) return null;
		if ("^".equals(returnPageName))
			return pageRenderLinkSource.createPageRenderLink(componentResources.getPage().getClass()).toAbsoluteURI();
		return pageRenderLinkSource.createPageRenderLink(returnPageName).toAbsoluteURI();

	}
}
