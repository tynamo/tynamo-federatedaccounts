package org.tynamo.security.federatedaccounts.openid.services;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.association.AssociationException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.MessageException;
import org.tynamo.security.federatedaccounts.openid.Provider;

public interface OpenidLoginManager {
	void requestAuthentication(Provider provider, String validateUrl);
		
	VerificationResult authenticate(HttpServletRequest httpServletRequest)  throws MessageException, DiscoveryException, AssociationException;
	
}
