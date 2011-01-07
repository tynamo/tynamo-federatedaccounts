package org.tynamo.security.federatedaccounts.facebook;

/*
 *
 * Nimble, an extensive application base for Grails
 * Copyright (C) 2010 Bradley Beddoes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.shiro.authc.AuthenticationToken;

public class FacebookConnectToken implements AuthenticationToken {

	private static final long serialVersionUID = -4132236873002954204L;

	private Object principal;

	private String credentials;

	public FacebookConnectToken(String code, String oauthToken) {
		this.principal = code;
		this.credentials = oauthToken;
	}

	/**
	 * Returns the Facebook sessionID associated with the current session
	 */
	public Object getPrincipal() {
		return this.principal;
	}

	/**
	 * Returns a map of key/values associated with Facebook session establishment (retrieved from cookies)
	 */
	public Object getCredentials() {
		return this.credentials;
	}
}