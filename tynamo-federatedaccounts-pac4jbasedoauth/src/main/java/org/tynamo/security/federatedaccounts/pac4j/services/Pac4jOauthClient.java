package org.tynamo.security.federatedaccounts.pac4j.services;

import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.client.OAuth10Client;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * Wrapper class for different versions of oauth client instances.
 * BaseOAuthCLient has been removed from Pac4j, so now we have to explicitly deal with two distinct versions of oauth clients.
 * This class wraps client instances and allows to avoid code duplication in Pac4jOauth.onOauthActivate() method.
 */
public class Pac4jOauthClient {

     private OAuth20Client<?> client20;
     private OAuth10Client<?> client10;

     public Pac4jOauthClient(OAuth20Client<?> client20) {
         this.client20 = client20;
         this.client10 = null;
     }

     public Pac4jOauthClient(OAuth10Client<?> client10) {
         this.client20 = null;
         this.client10 = client10;
     }

     public Object instance() {
         if (client20 != null) {
             return client20;
         }
         return client10;   
     }

     public String getName() {
         if (client20 != null) {
             return client20.getName();
         }
         return client10.getName();
     }

     public void setReadTimeout(final int timeout) {
         if (client20 != null) {
             client20.getConfiguration().setReadTimeout(timeout);
         } else {
             client10.getConfiguration().setReadTimeout(timeout);;
         }
     }

     public void setConnectTimeout(final int timeout) {
         if (client20 != null) {
             client20.getConfiguration().setConnectTimeout(timeout);
         } else {
             client10.getConfiguration().setConnectTimeout(timeout);;
         }
     }

     public void setCallbackUrl(final String callbackUrl) {
        if (client20 != null) {
             client20.setCallbackUrl(callbackUrl);
         } else {
             client10.setCallbackUrl(callbackUrl);
         }
     }

     public String getCallbackUrl() {
         if (client20 != null) {
             return client20.getCallbackUrl();
         }
         return client10.getCallbackUrl();
     }

     public String getRedirectionUrl(final WebContext context) throws HttpAction {
         if (client20 != null) {
             return client20.getRedirectAction(context).getLocation();
         }
         return client10.getRedirectAction(context).getLocation();
     }

     public final CommonProfile getUserProfile(final WebContext context) throws HttpAction {
         if (client20 != null) {
             OAuth20Credentials credentials = client20.getCredentials(context);
             return client20.getUserProfile(credentials, context);
         }
         OAuth10Credentials credentials = client10.getCredentials(context);
         return client10.getUserProfile(credentials, context); 
     }
}
