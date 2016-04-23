/**
 * This class has test scripts that tests the send mail and 
 * receive mail functionality for gmail. 
 * @author sghins
 * @version 1.0
 */

package com.webtest.pageTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.webtest.pageImpl.GmailPgImpl;

public class GmailSendReceiveTests extends CommonPageTests{
	final static Logger logger = Logger.getLogger(GmailSendReceiveTests.class);
	GmailPgImpl gmailPg = new GmailPgImpl();
	String configFile = null;
	InputStream input = null;
	Properties prop = new Properties();
	String subjectTimeStamp = "";
	
	/**
	 * Setup method for pre-test activities such as initializing the 
	 * driver based on browser and remote config settings 
	 */
	@BeforeSuite
	public void setUp() {
		logger.info("IN SETUP METHOD: performing pretest configurations");
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
		} catch (IOException e) {
			logger.error(e);
			
		}
		//Set the browser from the cmd line or from config.properties file
		String browser = System.getProperty("browser", prop.getProperty("browser"));
		logger.info("Selected browser is: " + browser);
		gmailPg.driverSetup(browser, prop.getProperty("remote"));
		gmailPg.explicitWaitSetup();
		gmailPg.loadXpathProps();
	}
	
	/**
	 * This method tests logging into gmail which the provided credentials
	 * @param username
	 * @param password
	 * @param expectedUrl
	 */
	@Test(dataProvider = "sendLoginData", priority = 0)
	public void testGmailloginCreds(String username, String password, String expectedUrl) {
		logger.info("TEST METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		gmailPg.loadGmailLoginPg();
		String currPageURL = gmailPg.verifyGmailSignInFlow(username, password, expectedUrl);
		Assert.assertTrue(currPageURL.startsWith(expectedUrl), "The current page url: '" + currPageURL + "' does not match the expected url: '" + expectedUrl + "'");
	}
	
	/**
	 * This methods tests the mail send functionality on gmail 
	 * @param to
	 * @param subject
	 * @param body
	 * @param expectedMsg
	 */
	@Test(dataProvider = "mailSendData", dependsOnMethods = {"testGmailloginCreds"}, priority = 1)
	public void testGmailSendFunctionality(String to, String subject, String body, String expectedMsg) throws InterruptedException{
		logger.info("TEST METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		Date date = new Date();
		subjectTimeStamp = new Timestamp(date.getTime()).toString();
		String subjTmStmp = subject + subjectTimeStamp;
		String actualMsg = gmailPg.verifyMailSendFunctionality(to, subjTmStmp, body);
		Assert.assertEquals(actualMsg, expectedMsg, "The actual message: '" + actualMsg + "' does not match the expected message: '" + expectedMsg + "'");
		
	}
	
	/**
	 * This method tests logging into a different gmail account for a user with an already 
	 * existing account username cached on the browser.
	 * @param username
	 * @param password
	 * @param expectedUrl
	 */
	@Test(dataProvider = "recvloginData", dependsOnMethods = {"testGmailSendFunctionality"}, priority = 2)
	public void testGmailloginWithDiffAccount(String username, String password, String expectedUrl) throws InterruptedException {
		logger.info("TEST METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		gmailPg.deleteAllCookies();
		gmailPg.loadGmailLoginPg();
		gmailPg.gmailAddNewAccount();
		String currPageURL = gmailPg.verifyGmailSignInFlow(username, password, expectedUrl);
		Assert.assertTrue(currPageURL.startsWith(expectedUrl), "The current page url: '" + currPageURL + "' does not match the expected url: '" + expectedUrl + "'");
	}
	
	/**
	 * This method verifies the a mail with the provided subject and body has been received
	 * @param subject
	 * @param body
	 */
	@Test(dataProvider = "mailRecvData", dependsOnMethods = {"testGmailloginWithDiffAccount"}, priority = 3)
	public void testGmailRecvFunctionality(String subject, String body){
		logger.info("TEST METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		String subjTimestamp = subject + subjectTimeStamp;
		String recvSubj = gmailPg.verifyRecvMailSubject(subjTimestamp);
		Assert.assertEquals(recvSubj, subjTimestamp, " The actual subject text: '" + recvSubj + "' does not match the expected subject text: '" + subjTimestamp + "'");
		String actualBody = gmailPg.getRecvMailBody();
		Assert.assertEquals(actualBody, body, " The actual body: '" + actualBody + "' does not match the expected body: '" + body + "'");
	}
	
	/**
	 * Teardown method for post test actions
	 */	
	@AfterSuite
	public void teardown() {
		logger.info("ALL TESTS COMPLETED: Quiting driver");
		gmailPg.quitDriver();
	}
	
	
}
