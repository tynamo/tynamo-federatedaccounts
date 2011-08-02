package org.tynamo.security.federatedaccounts.scribe.google;

import static org.tynamo.security.federatedaccounts.FederatedAccount.Type.google;

import java.util.Collection;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.PrincipalCollection;
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
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

		GoogleAuthenticationToken token = (GoogleAuthenticationToken) authenticationToken;
/*
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.google.com/m8/feeds/contacts/default/full");
		service.signRequest(token.getToken(), request);
		request.send().getBody();
*/

		// Null username is invalid, throw an exception if so - indicates that user hasn't granted the right
		// permissions (and/or we haven't asked for it)
/*
		if (facebookUser == null) throw new AccountException("Null Facebook user is not allowed by this realm.");

		String principalValue = null;
		switch (principalProperty) {
			case id: principalValue = facebookUser.getId();
				break;
			case email: principalValue = facebookUser.getEmail();
				break;
			case name: principalValue = facebookUser.getName();
				break;
		}
*/

//		return federatedAccountService.federate(google.name(), principalValue, authenticationToken, facebookUser);

		SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(authenticationToken.getPrincipal(), google.name());
		principalCollection.add(authenticationToken, google.name());
		return new SimpleAuthenticationInfo(principalCollection, authenticationToken.getCredentials());

	}

	/**
	 * FIXME The following operations should all be removed - https://issues.apache.org/jira/browse/SHIRO-231 requires
	 * AuthenticatingRealm to implement Authorizer, which is wrong. Remove when upgrading Shiro dependency to 1.2
	 */
	@Override
	public boolean isPermitted(PrincipalCollection principals, String permission) {
		return false;
	}

	@Override
	public boolean isPermitted(PrincipalCollection subjectPrincipal, Permission permission) {
		return false;
	}

	@Override
	public boolean[] isPermitted(PrincipalCollection subjectPrincipal, String... permissions) {
		return null;
	}

	@Override
	public boolean[] isPermitted(PrincipalCollection subjectPrincipal, List<Permission> permissions) {
		return null;
	}

	@Override
	public boolean isPermittedAll(PrincipalCollection subjectPrincipal, String... permissions) {
		return false;
	}

	@Override
	public boolean isPermittedAll(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) {
		return false;
	}

	@Override
	public void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {

	}

	@Override
	public void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException {

	}

	@Override
	public void checkPermissions(PrincipalCollection subjectPrincipal, String... permissions) throws AuthorizationException {

	}

	@Override
	public void checkPermissions(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) throws AuthorizationException {

	}

	@Override
	public boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
		return false;
	}

	@Override
	public boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
		return null;
	}

	@Override
	public boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
		return false;
	}

	@Override
	public void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {

	}

	@Override
	public void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {

	}

	@Override
	public void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {

	}

}
