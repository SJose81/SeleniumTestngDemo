package com.webtest.pageTest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;

public class CommonPageTests {
	
	@DataProvider(name="sendLoginData")
	public static Object[][] sendLoginData(){
		return new Object[][]{
				{"etouchint@gmail.com", "interview", "https://mail.google.com/mail/#inbox"}
		};
	}
	
	@DataProvider(name="recvloginData")
	public static Object[][] recvloginData(){
		return new Object[][]{
				{"viewinter6@gmail.com", "myintprep", "https://mail.google.com/mail/#inbox"}
		};
	}
	
	@DataProvider(name="mailSendData")
	public static Object[][] mailSendData(){
		return new Object[][]{
				{"viewinter6@gmail.com", "Test mail:", "Email body text", "Your message has been sent. View message"}
		};
	}
	
	@DataProvider(name="mailRecvData")
	public static Object[][] mailRecvData(){
		return new Object[][]{
				{"Test mail:", "Email body text"}
		};
	}

}
