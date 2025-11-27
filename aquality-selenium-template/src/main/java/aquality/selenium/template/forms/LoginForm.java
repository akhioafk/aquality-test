package aquality.selenium.template.forms;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ITextBox;
import org.openqa.selenium.By;

import static aquality.selenium.browser.AqualityServices.getElementFactory;

public class LoginForm {

    public void perform(String user, String pass, boolean register) {
        if (register) {
            ITextBox regUser = getElementFactory().getTextBox(By.id("reg_username"), "Registration Username");
            ITextBox regPass = getElementFactory().getTextBox(By.id("reg_password"), "Registration Password");
            ITextBox regEmail = getElementFactory().getTextBox(By.id("reg_email"), "Registration Email");
            IButton regButton = getElementFactory().getButton(By.id("registerBtn"), "Register Button");
            regUser.type(user);
            regPass.type(pass);
            regEmail.type(user + "@example.com");
            regButton.click();
        } else {
            ITextBox username = getElementFactory().getTextBox(By.id("username"), "Username");
            ITextBox password = getElementFactory().getTextBox(By.id("password"), "Password");
            IButton loginButton = getElementFactory().getButton(By.id("loginButton"), "Login Button");
            username.type(user);
            password.type(pass);
            loginButton.click();
        }
    }
}