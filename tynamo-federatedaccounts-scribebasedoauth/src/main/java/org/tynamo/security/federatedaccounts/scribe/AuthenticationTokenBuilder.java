package org.tynamo.security.federatedaccounts.scribe;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.model.Token;

public interface AuthenticationTokenBuilder {

	AuthenticationToken getAuthenticationToken(Token accessToken);

}
