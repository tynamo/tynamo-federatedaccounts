package org.tynamo.security.rollingtokens.services;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.tapestry5.jpa.annotations.CommitAfter;

public interface CommittingAuthenticationListener extends AuthenticationListener {

	@CommitAfter
	public void onLogout(PrincipalCollection principals);

	@CommitAfter
	public void onFailure(AuthenticationToken token, AuthenticationException ae);

	@CommitAfter
	public void onSuccess(AuthenticationToken token, AuthenticationInfo info);

}
