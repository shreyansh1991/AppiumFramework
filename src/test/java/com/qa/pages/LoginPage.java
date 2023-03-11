package com.qa.pages;

import com.qa.tests.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class LoginPage extends BaseTest {
    @AndroidFindBy(accessibility = "test-Username")
    private WebElement usernameTextField;
    @AndroidFindBy(accessibility = "test-Password")
    private WebElement passwordTextField;
    @AndroidFindBy(accessibility = "test-LOGIN")
    private WebElement loginButton;
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
    private WebElement errorText;

    public LoginPage enterUserName(String username) {
        sendKeys(usernameTextField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {

        sendKeys(passwordTextField, password);
        return this;
    }

    public ProductsPage pressLoginButton() {
        click(loginButton);
        return new ProductsPage();
    }

    public ProductsPage login(String username, String password) {
        enterUserName(username);
        enterPassword(password);
        return pressLoginButton();
    }

    public String getErrorText() {
        return getAttribute(errorText, "text");
    }


}
