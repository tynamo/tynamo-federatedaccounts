package org.tynamo.security.rollingtokens.internal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.slf4j.Logger;
import org.tynamo.security.rollingtokens.services.RollingToken;

public class RollingTokenAutoLoginAdvice implements MethodAdvice {
	private final Logger logger;
	private final HttpServletRequest request;
	private String realmName;
	private Class<?> principalType;

	public RollingTokenAutoLoginAdvice(Logger logger, HttpServletRequest request, String realmName, Class<?> principalType)
		throws ClassNotFoundException {
		this.logger = logger;
		this.request = request;
		this.realmName = realmName;
		this.principalType = principalType;
	}

	@Override
	public void advise(MethodInvocation invocation) {
		invocation.proceed();
		// SubjectContext context = (SubjectContext) invocation.getParameter(0);
		// if (!(context instanceof WebSubjectContext)) { return; }
		// WebSubjectContext wsc = (WebSubjectContext) context;
		// PrincipalCollection principals = wsc.resolvePrincipals();
		// boolean authenticated = wsc.resolveAuthenticated();
		Subject subject = (Subject) invocation.getReturnValue();
		PrincipalCollection principals = subject.getPrincipals();
		if (principals == null || subject.isAuthenticated()) return;

		String rollingTokenValue = null;
		try {
			// one issue is that underlying request shadow may at times be null
			// A bigger problem is that when security is decorating the request, it needs a subject
			// but we are only building it here. So we can't use Request shadow
			Cookie[] cookies = request.getCookies();
			// System.out.println("request " + request + " " + request.getCookies());
			for (Cookie cookie : cookies)
				if (RollingToken.TOKEN_NAME.equals(cookie.getName())) {
					rollingTokenValue = cookie.getValue();
					break;
				}
		} catch (RuntimeException e) {
			// ignore
		}
		if (rollingTokenValue == null) return;

		Object principal = RollingToken.getConfiguredPrincipal(realmName, principalType, principals);

		RollingToken token = new RollingToken(principal, rollingTokenValue, request.getRemoteAddr());
		try {
			subject.login(token);
			logger.info("Rolling token authentication using token " + rollingTokenValue
				+ " succeeded for user identified by " + principal);
		} catch (AuthenticationException e) {
			logger.info("Rolling token authentication using token " + rollingTokenValue + " failed for user identified by "
				+ principal);
		}

	}

}
