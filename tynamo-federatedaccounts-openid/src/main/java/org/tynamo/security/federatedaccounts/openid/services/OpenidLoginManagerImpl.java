package org.tynamo.security.federatedaccounts.openid.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Response;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.openid.Provider;

public class OpenidLoginManagerImpl implements OpenidLoginManager {
	// services
	private Logger logger;
	private ApplicationStateManager applicationStateManager;
	private Response response;

	// other
	private ConsumerManager consumerManager;

	private static final String NAJDI_DISCOVERY_URL = "https://id.najdi.si/discovery";
	private static final String GOOGLE_DISCOVERY_URL = "https://www.google.com/accounts/o8/id";

	public OpenidLoginManagerImpl(Logger logger, ApplicationStateManager applicationStateManager, Response response) {
		this.logger = logger;
		this.applicationStateManager = applicationStateManager;
		this.response = response;

		logger.info("Preparing consumerManager...");
		this.consumerManager = new ConsumerManager();
		consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
		consumerManager.setNonceVerifier(new InMemoryNonceVerifier(5000));
	}

	public void requestAuthentication(Provider provider, String validateUrl) {

		String discovery = NAJDI_DISCOVERY_URL;
		if (Provider.google.equals(provider)) {
			discovery = GOOGLE_DISCOVERY_URL;
		}

		try {
			List discoveries = consumerManager.discover(discovery);
			for (Object o : discoveries)
				logger.info(" **** " + o.getClass().getCanonicalName());

			DiscoveryInformation discovered = consumerManager.associate(discoveries);

			applicationStateManager.set(DiscoveryInformation.class, discovered);

			// store the discovery information in the user's session
			// session.setAttribute("openid-disco", discovered);

			// obtain a AuthRequest message to be sent to the OpenID provider

			AuthRequest authReq = consumerManager.authenticate(discovered, validateUrl);

			// logger.info(" ENDPOINT: "+authReq.getOPEndpoint());

			FetchRequest fetch = FetchRequest.createFetchRequest();
			fetch.addAttribute("username", "http://schema.openid.net/namePerson/friendly", true);
			fetch.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
			fetch.addAttribute("role", "http://schema.prijava.najdi.si/role", true);
			fetch.addAttribute("user", "openid.ns.user", true);
			// fetch.addAttribute("email", "http://openid.net/schema/contact/internet/email", true);

			authReq.addExtension(fetch);

			response.sendRedirect(authReq.getDestinationUrl(true));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error discovering openId service", e.getMessage());
		}
	}

	public VerificationResult authenticate(HttpServletRequest httpServletRequest) throws MessageException,
		DiscoveryException, AssociationException {

		ParameterList openidResp = new ParameterList(httpServletRequest.getParameterMap());

		StringBuffer receivingURL = httpServletRequest.getRequestURL();
		String queryString = httpServletRequest.getQueryString();
		DiscoveryInformation discovered = applicationStateManager.get(DiscoveryInformation.class);
		VerificationResult verification = consumerManager.verify(receivingURL.toString(), openidResp, discovered);

		for (Object e : verification.getAuthResponse().getParameterMap().entrySet()) {
			logger.info("  *  " + e);
		}

		return verification;
	}

}
