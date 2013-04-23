package org.tynamo.security.federatedaccounts.base;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AbstractOauthSignInUnitTest {
	Link link = mock(Link.class);
	AbstractOauthSignIn oauthSignIn = new AbstractOauthSignIn() {

		@Override
		protected Class getOauthPageClass() {
			return getClass();
		}
	};

	@BeforeMethod
	public void setUp() {
		oauthSignIn.baseURLSource = mock(BaseURLSource.class);
		oauthSignIn.request = mock(Request.class);
		oauthSignIn.pageRenderLinkSource = mock(PageRenderLinkSource.class);
		oauthSignIn.baseURLSource = mock(BaseURLSource.class);
		oauthSignIn.componentResources = mock(ComponentResources.class);

		when(oauthSignIn.pageRenderLinkSource.createPageRenderLink((Class) any())).thenReturn(link);
		when(oauthSignIn.pageRenderLinkSource.createPageRenderLink((String) any())).thenReturn(link);
		Component page = mock(Component.class);
		when(oauthSignIn.componentResources.getPage()).thenReturn(page);
	}

	@Test
	public void getReturnPageUriWithEmptyReturnPage() {
		oauthSignIn.defaultReturnPage = "";
		oauthSignIn.returnPageName = "";

		when(oauthSignIn.baseURLSource.getBaseURL(false)).thenReturn("http://givemethebaseurl.com/");

		assertEquals("http://givemethebaseurl.com/", oauthSignIn.getReturnPageUri());
	}

	@Test
	public void getReturnPageUriWithReturnPageSpecified() {
		oauthSignIn.returnPageName = "home";

		when(link.toAbsoluteURI()).thenReturn("http://foo.com/home");
		assertEquals("http://foo.com/home", oauthSignIn.getReturnPageUri());
		// we cannot test for defaultReturnPage unless we processed the Tapestry annotation one way or another
	}

	@Test
	public void getReturnPageUriForContainingPage() {
		oauthSignIn.returnPageName = "^";

		when(link.toAbsoluteURI()).thenReturn("http://foo.com/some");
		assertEquals("http://foo.com/some", oauthSignIn.getReturnPageUri());
	}

}
