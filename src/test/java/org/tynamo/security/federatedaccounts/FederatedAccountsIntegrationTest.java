/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.tynamo.security.federatedaccounts;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class FederatedAccountsIntegrationTest extends AbstractContainerTest {

	private static final String STATUS_NOT_AUTH = "STATUS[Not Authenticated]";
	private static final String STATUS_AUTH = "STATUS[Authenticated]";
	private HtmlPage page;

	@Override
	@BeforeClass
	public void configureWebClient() {
		webClient.setThrowExceptionOnFailingStatusCode(false);
	}

	@Override
	public WebAppContext buildContext() {
		WebAppContext context = new WebAppContext("src/test/webapp", "/");
		/*
		 * Sets the classloading model for the context to avoid an strange "ClassNotFoundException: org.slf4j.Logger"
		 */
		context.setParentLoaderPriority(true);
		return context;
	}

	@BeforeGroups(groups = "notLoggedIn")
	public void checkLoggedIn() throws Exception {
		openBase();

		if (STATUS_AUTH.equals(getText("status"))) {
			clickOnBasePage("tynamoLogout");
		}
	}

	// ----------------------------------------

	@Test(dependsOnGroups = { "notLoggedIn" })
	public void testLoginClick() throws Exception {
		clickOnBasePage("tynamoLoginLink");
		assertLoginPage();
	}

	@Test(dependsOnMethods = "testLoginClick")
	public void testLogin() throws Exception {
		loginAction();
		openBase();
		assertAuthenticated();
	}

	// ----------------------------------------

	@Test(dependsOnGroups = { "loggedIn" })
	public void testLogout() throws Exception {
		clickOnBasePage("tynamoLogoutLink");
		assertNotAuthenticated();
	}

	protected void assertLoginPage() {
		assertNotNull(page.getElementById("tynamoLogin"), "Page doesn't contain login field. Not a login page.");
		assertEquals("password", getAttribute("tynamoPassword", "type"), "Page doesn't contain password field. Not a login page.");
		assertEquals("checkbox", getAttribute("tynamoRememberMe", "type"), "Page doesn't contain rememberMe field. Not a login page.");

		assertNotNull(page.getElementById("tynamoEnter"), "Page doesn't contain login form submit button. Not a login page.");
	}

	// -----------------------

	private String getAttribute(String id, String attr) {
		return page.getElementById(id).getAttribute(attr);
	}

	protected void assertUnauthorizedPage() {
		assertEquals(getTitle(), "Unauthorized", "Not Unauthorized page");
	}

	protected void assertUnauthorizedPage401() {
		assertEquals(getTitle(), "Error 401 UNAUTHORIZED", "Not Unauthorized page");
	}

	protected void openPage(String url) throws Exception {
		page = webClient.getPage(BASEURI + url);
	}

	protected void openBase() throws Exception {
		openPage("");
	}

	protected void clickOnBasePage(String url) throws Exception {
		openBase();
		page = page.getElementById(url).click();
	}

	protected void assertAuthenticated() {
		assertEquals(getText("status"), STATUS_AUTH);
	}

	protected void assertNotAuthenticated() {
		assertEquals(getText("status"), STATUS_NOT_AUTH);
	}

	protected void loginAction() throws IOException {
		type("tynamoLogin", "psycho");
		type("tynamoPassword", "psycho");
		click("tynamoEnter");
	}

	private void type(String id, String value) {
		page.getForms().get(0).<HtmlInput> getInputByName(id).setValueAttribute(value);
	}

	private void click(String id) throws IOException {
		page = clickButton(page, id);
	}

	private String getText(String id) {
		return page.getElementById(id).asText();
	}

	private void assertText(String id, String text) {
		assertEquals(page.getElementById(id).asText(), text);
	}

	private boolean isElementPresent(String id) {
		return page.getElementById(id) != null;
	}

	private String getTitle() {
		return page.getTitleText();
	}

	private String getLocation() {
		return page.getWebResponse().getRequestSettings().getUrl().toString();
	}

}
