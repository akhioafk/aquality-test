package aquality.selenium.template.cucumber.stepdefinitions.ui;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import java.util.List;

public class ElementSteps {
    
    private WebDriver driver;
    
    @Given("I wait for {int} seconds")
    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @When("I click on element with id {string}")
    public void clickElementById(String elementId) {
        WebElement element = driver.findElement(By.id(elementId));
        element.click();
    }
    
    @Then("I verify element with xpath {string} is visible")
    public void verifyElementIsVisible(String xpath) {
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        if (elements.size() == 0) {
            throw new RuntimeException("Element not found");
        }
        if (!elements.get(0).isDisplayed()) {
            throw new RuntimeException("Element is not visible");
        }
    }
    
    @When("I enter text {string} into field with name {string}")
    public void enterTextIntoField(String text, String fieldName) {
        WebElement field = driver.findElement(By.name(fieldName));
        field.clear();
        field.sendKeys(text);
    }
    
    @Then("page title should be {string}")
    public void verifyPageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        if (!actualTitle.equals(expectedTitle)) {
            System.out.println("Title mismatch: expected " + expectedTitle + " but got " + actualTitle);
        }
    }
}
