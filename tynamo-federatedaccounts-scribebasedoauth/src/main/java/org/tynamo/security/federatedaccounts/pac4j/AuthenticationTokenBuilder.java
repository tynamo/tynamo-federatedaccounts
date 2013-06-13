package org.tynamo.security.federatedaccounts.pac4j;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.model.Token;

public interface AuthenticationTokenBuilder {

	AuthenticationToken getAuthenticationToken(Token accessToken);

}
