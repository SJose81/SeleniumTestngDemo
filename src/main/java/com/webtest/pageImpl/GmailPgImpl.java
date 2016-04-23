/**
 * This class has the implementation methods for gmail page 
 * login, send and receive functionality. It also includes webdriver
 * setup functionality
 */
package com.webtest.pageImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GmailPgImpl {
	final static Logger logger = Logger.getLogger(GmailPgImpl.class);
	static WebDriver  driver = null;
	static WebDriverWait expWait = null;
	static String browser = null;
	Properties props = new Properties();
	String hubUrl = "http://localhost:4444/wd/hub";
	String gmailLoginUrl = "https://www.gmail.com";
		
	/**
	 * Method to setup driver based on the parameters provided
	 * @param browserName
	 * @param remote
	 */
	public void driverSetup(String browserName, String remote){
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		browser = browserName;
		if(remote.equalsIgnoreCase("remote")){
			setRemoteWebDriver(browser);	
		}else{
			if(browser.equalsIgnoreCase("firefox")){
				driver = new FirefoxDriver();
			}else if(browser.equalsIgnoreCase("chrome")){
				driver = new ChromeDriver();
			}
		}
	}
	
	/**
	 * Method to setup remotewebdriver based on the browser data provided
	 * @param browserName
	 */
	void setRemoteWebDriver(String browserName) {
		try{
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());	
		if(browserName.equalsIgnoreCase("firefox")){
			driver = new RemoteWebDriver(new URL(hubUrl), DesiredCapabilities.firefox());
		}else if(browserName.equalsIgnoreCase("chrome")){
			driver = new RemoteWebDriver(new URL(hubUrl), DesiredCapabilities.chrome());
		}else if(browserName.equalsIgnoreCase("safari")){
			driver = new RemoteWebDriver(new URL(hubUrl), DesiredCapabilities.safari());
		}else{
			driver = new RemoteWebDriver(new URL(hubUrl), DesiredCapabilities.firefox());
		}
		}catch(MalformedURLException ex){
			logger.error(ex);
		}
		
	}
	
	/**
	 * Method to load the xpath property file
	 */
	public void loadXpathProps(){
		try {
			props.load(new FileInputStream("src/main/resources/gmailxpath.properties"));
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	/**
	 * Method to setup explicit wait
	 */
	public void explicitWaitSetup(){
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		expWait = new WebDriverWait(driver, 10);
	}
	
	/**
	 * Method to load the gmail homepage
	 * @return
	 */
	public String loadGmailLoginPg() {
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.get(gmailLoginUrl);
		return driver.getCurrentUrl();
	}
	
	/**
	 * Method to login to a gamil account
	 * @param username
	 * @param password
	 * @param expectedUrl
	 * @return
	 */
	public String verifyGmailSignInFlow(String username, String password, String expectedUrl) {
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.findElement(By.id("Email")).sendKeys(username);
		driver.findElement(By.id("next")).click();
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.id("Passwd")));
		
		driver.findElement(By.id("Passwd")).sendKeys(password);
		driver.findElement(By.id("signIn")).click();
		//expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='https://mail.google.com/mail/#inbox']")));
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(props.getProperty("inboxLink"))));
		
		return driver.getCurrentUrl();
	}
	
	/**
	 * Method to add a new gmail account
	 * @return
	 */
	public String gmailAddNewAccount() {
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		if(browser.equalsIgnoreCase("chrome")){
			expWait.until(ExpectedConditions.presenceOfElementLocated(By.id("gmail-sign-in")));
			driver.findElement(By.id("gmail-sign-in")).click();
		}
		
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.id("account-chooser-link")));
		driver.findElement(By.id("account-chooser-link")).click();
		
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.id("account-chooser-add-account")));
		driver.findElement(By.id("account-chooser-add-account")).click();
		return driver.getCurrentUrl();
		
	}
	
	/**
	 * This method sends the mail with parameters provided
	 * @param to
	 * @param subjTmstmp
	 * @param body
	 * @return
	 * @throws InterruptedException
	 */
	public String verifyMailSendFunctionality(String to, String subjTmstmp, String body) throws InterruptedException{
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		driver.findElement(By.xpath(props.getProperty("composeBtn"))).click();
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(props.getProperty("toTextarea"))));
		driver.findElement(By.xpath(props.getProperty("toTextarea"))).sendKeys(to);
		driver.findElement(By.xpath(props.getProperty("subjTextBox"))).sendKeys(subjTmstmp);
		driver.findElement(By.xpath(props.getProperty("bodyTextarea"))).sendKeys(body);
		driver.findElement(By.xpath(props.getProperty("sendBtn"))).click();
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(props.getProperty("sendConfMsgText"))));
		return driver.findElement(By.xpath(props.getProperty("sendConfMsgText"))).getText();
	}

	/**
	 * this method verifies that mail with subject passed as parameter, exists in the inbox
	 * and return the subject
	 * @param subjectWithTimestamp
	 * @return
	 */
	public String verifyRecvMailSubject(String subjectWithTimestamp){
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		driver.findElement(By.xpath(props.getProperty("inboxLink"))).click();
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span//b[contains(text(), '" + subjectWithTimestamp + "')]")));
		driver.findElement(By.xpath("//span//b[contains(text(),'" + subjectWithTimestamp + "')]")).click();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(props.getProperty("mailSubjHeading"))));
		return driver.findElement(By.xpath(props.getProperty("mailSubjHeading"))).getText();
	}
	
	/**
	 * this method return the body of the email
	 * @return
	 */
	public String getRecvMailBody(){
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		expWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(props.getProperty("mailBodyTxt"))));
		return driver.findElement(By.xpath(props.getProperty("mailBodyTxt"))).getText();
	}
	
	/**
	 * method to delete all cookies for the current domain
	 */
	public void deleteAllCookies() {
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.manage().deleteAllCookies();
	}
	
	/**
	 * method to close the current browser window
	 */
	public void quitDriver() {
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.quit();
	}
	
	/**
	 * method to quit the driver
	 */
	public void closeDriver() {
		logger.info("METHOD: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		driver.close();
	}
	

	
	
	
	
}
