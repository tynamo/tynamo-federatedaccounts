package org.tynamo.security.federatedaccounts;

public interface FederatedAccount {

	public enum FederatedAccountType {
		facebook, google, twitter, openid, github
	}

	public boolean isAccountLocked();

	public void setAccountLocked(boolean value);

	public boolean isCredentialsExpired();

	public void setCredentialsExpired(boolean value);

	public boolean federate(String realmName, Object remotePrincipal, Object remoteAccount);
}
