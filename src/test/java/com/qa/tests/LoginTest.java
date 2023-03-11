package com.qa.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LoginTest extends BaseTest {
    static Logger logger = LogManager.getLogger(LoginTest.class.getName());
    LoginPage loginPage;
    ProductsPage productsPage;
    Map<String, Map<String, Object>> map
            = null;

    @BeforeClass
    public void beforeClass() {
        ObjectMapper objectMapper = new ObjectMapper();
        String filePath = getClass().getClassLoader().getResource("data/loginUsers.json").getFile();
        try {
            map = objectMapper.readValue(new File(filePath), new TypeReference<Map<String, Map<String, Object>>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        closeApp();
        launchApp();
    }

    @BeforeMethod
    public void beforeMethod() {
        loginPage = new LoginPage();
    }

    @AfterMethod
    public void afterMethod() {
        closeApp();
        launchApp();
    }

    @Test
    public void invalidUsername() {
        logger.info("started...");
        loginPage.enterUserName(map.get("invalidUser").get("username").toString()).
                enterPassword(map.get("invalidUser").get("password").toString()).pressLoginButton();
        String errorText = loginPage.getErrorText();
        Assert.assertEquals(errorText, "Username and password do not match any user in this service.");
        logger.info("ended...");
    }

    @Test
    public void invalidPassword() throws InterruptedException {
        loginPage.enterUserName(map.get("invalidPassword").get("username").toString()).
                enterPassword(map.get("invalidPassword").get("password").toString()).pressLoginButton();

        Assert.assertEquals(loginPage.getErrorText(), "1Username and password do not match any user in this service.");
    }

    @Test
    public void login() {
        String productsPageTitle = loginPage.enterUserName(map.get("validUser").get("username").toString()).
                enterPassword(map.get("validUser").get("password").toString()).pressLoginButton().getTitle();
        Assert.assertEquals(productsPageTitle, "1PRODUCTS");
    }
}
