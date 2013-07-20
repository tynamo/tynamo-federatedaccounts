package org.tynamo.security.federatedaccounts.testapp.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.eclipse.persistence.annotations.Index;
import org.tynamo.security.federatedaccounts.FederatedAccount;

@Entity
public class User implements FederatedAccount {
	public static enum Role {
		enduser(1), admin(5);
		private int weight;

		Role(int weight) {
			this.weight = weight;
		}

		public int weight() {
			return weight;
		}
	}

	private Integer id;

	private Long facebookUserId;
	private Long twitterUserId;
	private String dropboxUserId;

	private String username;

	private String firstName;

	private String lastName;

	private String emailAddress;

	private String encodedPassword;

	private Date created = new Date();

	private boolean accountLocked;

	private boolean credentialsExpired;

	private Set<Role> roles = new HashSet<Role>();

	private byte[] passwordSalt;

	private String yahooUserId;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@NonVisual
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return (obj instanceof User && ((User) obj).getUsername().equals(username));
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return username == null ? 0 : username.hashCode();
	}

	@Column(unique = true)
	@Index(name = "User_username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Validate("required,regexp=^[0-9a-zA-Z._%+-]+@[0-9a-zA-Z]+[\\.]{1}[0-9a-zA-Z]+[\\.]?[0-9a-zA-Z]+$")
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@NonVisual
	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	@NonVisual
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}

	public void setCredentialsExpired(boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@ElementCollection
	public Set<Role> getRoles() {
		return roles;
	}

	@Transient
	public String getPassword() {
		return "";
	}

	public void setPassword(String password) {
		if (password != null && !password.equals(encodedPassword) && !"".equals(password)) {
			ByteSource saltSource = new SecureRandomNumberGenerator().nextBytes();
			this.passwordSalt = saltSource.getBytes();
			this.encodedPassword = new Sha1Hash(password, saltSource).toString();
		}
	}

	@Override
	public String toString() {
		return "User " + username;
	}

	public void setPasswordSalt(byte[] passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@NonVisual
	@Column(length = 128)
	public byte[] getPasswordSalt() {
		return passwordSalt;
	}

	@Override
	public boolean federate(String realmName, Object remotePrincipal, Object remoteAccount) {
		if (remoteAccount instanceof com.restfb.types.User) {
			// remotePrincipal is null, this is a federated account update
			if (remotePrincipal == null) {
				// update federated/overlapping properties
			} else {
				// newly created account but oviously you could also check if (local) id is null
				// If you don't allow auto-federation and didn't implement a custom FederatedAccountService,
				// you could throw an exception here or initialize the account in locked state
				com.restfb.types.User fbUser = (com.restfb.types.User) remoteAccount;
				facebookUserId = Long.valueOf(fbUser.getId());
				// initialize other federated/overlapping properties
			}
		}
		return false;
	}

	public void setFacebookUserId(Long facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	@NonVisual
	@Column(unique = true, nullable = true)
	public Long getFacebookUserId() {
		return facebookUserId;
	}

	@NonVisual
	@Column(unique = true, nullable = true)
	public Long getTwitterUserId() {
		return twitterUserId;
	}

	public void setTwitterUserId(Long twitterUserId) {
		this.twitterUserId = twitterUserId;
	}

	@NonVisual
	@Column(unique = true, nullable = true)
	public String getDropboxUserId() {
		return dropboxUserId;
	}

	public void setDropboxUserId(String dropboxUserId) {
		this.dropboxUserId = dropboxUserId;
	}

	@NonVisual
	@Column(unique = true, nullable = true)
	public String getYahooUserId() {
		return yahooUserId;
	}

	public void setYahooUserId(String yahooUserId) {
		this.yahooUserId = yahooUserId;
	}

	@Override
	@Transient
	public Object getLocalAccountPrimaryPrincipal() {
		return getId();
	}

}
