package com.qa.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import com.qa.utils.TestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class ProductTests extends BaseTest {
	static Logger logger = LogManager.getLogger(ProductTests.class.getName());
	LoginPage loginPage;
	ProductsPage productsPage;
	SettingsPage settingsPage;
	ProductDetailsPage productDetailsPage;

	Map<String, Map<String, Object>> map
			= null;
	TestUtils utils = new TestUtils();

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

	  @AfterClass
	  public void afterClass() {
	  }
	  
	  @BeforeMethod
	  public void beforeMethod(Method m) {
		  loginPage = new LoginPage();
		  productsPage=  loginPage.enterUserName(map.get("validUser").get("username").toString()).
				  enterPassword(map.get("validUser").get("password").toString()).pressLoginButton();
 }

	  @AfterMethod
	  public void afterMethod() {

//		  settingsPage = productsPage.pressSettingsBtn();
//		  loginPage = settingsPage.pressLogoutBtn();
		  closeApp();
		  launchApp();
	  }
	  
	  @Test
	  public void validateProductOnProductsPage() {
		  SoftAssert sa = new SoftAssert();
		  logger.info("validate..");
		  String SLBTitle = productsPage.getSLBTitle();
		  System.out.println(SLBTitle);
		  sa.assertEquals(SLBTitle, "Sauce Labs Backpack");
		  
		  String SLBPrice = productsPage.getSLBPrice();
		  System.out.println(SLBPrice);
		  sa.assertEquals(SLBPrice, "$29.99");
		  
		  sa.assertAll();
	  }
	  
	  @Test
	  public void validateProductOnProductDetailsPage() {
		  SoftAssert sa = new SoftAssert();
		  logger.info("validate1..");
		  productDetailsPage = productsPage.pressSLBTitle();

		  String SLBTitle = productDetailsPage.getSLBTitle();
		  System.out.println(SLBTitle);
		  sa.assertEquals(SLBTitle, "Sauce Labs Backpack");

		  String SLBTxt = productDetailsPage.getSLBTxt();
		  System.out.println(SLBTxt);
		  sa.assertEquals(SLBTxt, "carry.allTheThings() with the sleek, streamlined Sly Pack that melds uncompromising style with unequaled laptop and tablet protection.");

		  String SLBPrice = productDetailsPage.scrollToSLBPriceAndGetSLBPrice();
		  System.out.println("SLB Price :: "+SLBPrice);
		  sa.assertEquals(SLBPrice, "$29.99");
		  sa.assertAll();

		  //  if(getPlatform().equalsIgnoreCase("Android")) {
		//	  String SLBPrice = productDetailsPage.scrollToSLBPriceAndGetSLBPrice();
		//	  sa.assertEquals(SLBPrice, "");
//		  }
//		  if(getPlatform().equalsIgnoreCase("iOS")) {
//			  String SLBTxt = productDetailsPage.getSLBTxt();
//			  sa.assertEquals(SLBTxt, getStrings().get("product_details_page_slb_txt"));

//			  productDetailsPage.scrollPage();
//			  sa.assertTrue(productDetailsPage.isAddToCartBtnDisplayed());
		  }
//		  productsPage = productDetailsPage.pressBackToProductsBtn(); // -> Commented as this is causing stale element exception for the Settings icon


	  }