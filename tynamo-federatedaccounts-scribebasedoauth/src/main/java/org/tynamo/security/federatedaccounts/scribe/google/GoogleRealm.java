package org.tynamo.security.federatedaccounts.scribe.google;

import static org.tynamo.security.federatedaccounts.FederatedAccount.Type.google;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

/**
 * <p/>
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Google.
 */
public class GoogleRealm extends AuthenticatingRealm {

	private Logger logger;
	private Google20Service service;

	public static enum PrincipalProperty {
		id, email, name
	}

	private PrincipalProperty principalProperty;

	private FederatedAccountService federatedAccountService;

	public GoogleRealm(Logger logger, FederatedAccountService federatedAccountService, Google20Service service) {

		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;
		this.service = service;

		String principalPropertyName = "id";

		// Let this throw IllegalArgumentException if value is not supported
		this.principalProperty = PrincipalProperty.valueOf(principalPropertyName);

		setName(google.name());
		setAuthenticationTokenClass(GoogleAuthenticationToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
		throws AuthenticationException {

		GoogleAuthenticationToken token = (GoogleAuthenticationToken) authenticationToken;
		/*
		 * OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.google.com/m8/feeds/contacts/default/full");
		 * service.signRequest(token.getToken(), request); request.send().getBody();
		 */

		// Null username is invalid, throw an exception if so - indicates that user hasn't granted the right
		// permissions (and/or we haven't asked for it)
		/*
		 * if (facebookUser == null) throw new AccountException("Null Facebook user is not allowed by this realm.");
		 * 
		 * String principalValue = null; switch (principalProperty) { case id: principalValue = facebookUser.getId(); break; case email:
		 * principalValue = facebookUser.getEmail(); break; case name: principalValue = facebookUser.getName(); break; }
		 */

		// return federatedAccountService.federate(google.name(), principalValue, authenticationToken, facebookUser);

		SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(authenticationToken.getPrincipal(),
			google.name());
		principalCollection.add(authenticationToken, google.name());
		return new SimpleAuthenticationInfo(principalCollection, authenticationToken.getCredentials());

	}
}
