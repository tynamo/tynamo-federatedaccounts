package org.tynamo.security.federatedaccounts.openid;
import org.apache.shiro.authc.AuthenticationToken;
import org.openid4java.consumer.VerificationResult;

public class OpenidAccessToken implements AuthenticationToken {

	private static final long serialVersionUID = 0L;
	private String identity;
	private VerificationResult verificationResult;
	
	public OpenidAccessToken(String identity, VerificationResult verificationResult) {
		this.identity=identity;		
		this.setVerificationResult(verificationResult);
	}

	@Override
	public Object getPrincipal() {
		return identity;
	}

	@Override
	public Object getCredentials() {
		return identity;
	}

	public VerificationResult getVerificationResult() {
		return verificationResult;
	}

	public void setVerificationResult(VerificationResult verificationResult) {
		this.verificationResult = verificationResult;
	}

}
