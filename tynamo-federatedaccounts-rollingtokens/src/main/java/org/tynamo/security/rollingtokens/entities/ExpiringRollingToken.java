package org.tynamo.security.rollingtokens.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
// FIXME Look into combining both ExpiringRollingToken and UsernameRollingToken into one
public class ExpiringRollingToken {
	private Long id;
	private String name;
	private String value;
	private Date expiresAfter = new Date();
	private String hostAddress;

	public ExpiringRollingToken() {
	}

	public ExpiringRollingToken(String name, String value, String hostAddress, Date expiresAfter) {
		this();
		this.name = name;
		this.value = value;
		this.hostAddress = hostAddress;
		setExpiresAfter(expiresAfter);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String token) {
		this.value = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpiresAfter() {
		return expiresAfter;
	}

	public void setExpiresAfter(Date expiresAfter) {
		if (expiresAfter != null) this.expiresAfter = expiresAfter;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getHostAddress() {
		return hostAddress;
	}
}
