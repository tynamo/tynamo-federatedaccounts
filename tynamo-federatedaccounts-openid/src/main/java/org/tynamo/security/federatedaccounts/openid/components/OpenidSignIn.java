package org.tynamo.security.federatedaccounts.openid.components;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.openid.base.OpenidAuthComponentBase;
import org.tynamo.security.federatedaccounts.openid.services.OpenidLoginManager;
import org.tynamo.security.federatedaccounts.openid.services.OpenidRealm;
import org.tynamo.security.federatedaccounts.openid.util.Provider;
import org.tynamo.security.federatedaccounts.util.WindowMode;

@Import(library = "OpenidSignIn.js", stylesheet = "button.css")
public class OpenidSignIn extends OpenidAuthComponentBase {

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
	public void componentStartup(){
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		StringBuilder sb=new StringBuilder();
		sb.append(request.getRequestURL());
		if(StringUtils.isNotBlank(request.getQueryString())){
			sb.append("?");
			sb.append(request.getQueryString());
		}
		currentUrl=sb.toString();
		
		//logger.info("Activating OpenIdSignIn component: " + currentUrl);
	}
	
	public void onAction(String currentPage){
		logger.info("Clicked link: "+provider);
		logger.info(" ******** current url: "+currentPage);
				
		loginManager.requestAuthentication(provider, getAuthRedirectLink(currentPage));
	}
	
	
	public boolean getIsProviderNajdi(){
		if(provider!=null){
			return provider.equals(Provider.najdi);
		}
		return false;
	}
	
	public boolean getIsProviderGoogle(){
		if(provider!=null){
			return provider.equals(Provider.google);
		}
		return false;
	}
	
	public boolean getIsProviderGeneral(){
		return !getIsProviderGoogle() && !getIsProviderNajdi();
	}
	
	
}
