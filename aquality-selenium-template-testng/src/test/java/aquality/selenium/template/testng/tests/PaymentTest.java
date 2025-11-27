package aquality.selenium.template.testng.tests;

import aquality.selenium.browser.AqualityServices;

import org.openqa.selenium.By;
import aquality.selenium.elements.interfaces.ITextBox;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PaymentTest {
    @BeforeClass
    public void setUp() {
        AqualityServices.getBrowser().maximize();
        AqualityServices.getBrowser().goTo("https://example.com/login");
    }

    @Test
    public void testSuccessfulLogin() {
        ITextBox username = AqualityServices.getElementFactory().getTextBox(By.id("username"), "Username");
        ITextBox password = AqualityServices.getElementFactory().getTextBox(By.id("password"), "Password");
        username.sendKeys("user1");
        password.sendKeys("pass1");
        AqualityServices.getBrowser().getDriver().findElement(By.id("loginButton")).click();
    }

    @AfterClass
    public void tearDown() {
        AqualityServices.getBrowser().quit();
    }
}












