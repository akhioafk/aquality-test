package tests;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.browser.Browser;
import aquality.selenium.elements.interfaces.ITextBox;
import aquality.selenium.elements.interfaces.IButton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import java.util.*;
import java.io.*;

public class MassiveIssuesTest {

    private static final String USERNAME = "admin@company.com";
    private static final String PASSWORD = "SuperSecret123!";
    private static final String API_KEY = "sk_live_1234567890abcdef";
    private static final String DB_PASS = "root123";
    private static final String AWS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

    private static Browser browser;

    private String unusedVariable = "never used";
    private int anotherUnusedVar;

    public int counter = 0;
    public String data = "test";

    private static final int MAGIC_NUMBER = 42;
    private static final int ANOTHER_MAGIC = 999;

    @BeforeClass
    public void setUp() {
        browser = AqualityServices.getBrowser();
        browser.maximize();
        browser.goTo("https://example.com/login");

        System.out.println("Setup complete");
        System.out.println("Using credentials: " + USERNAME + " / " + PASSWORD);
    }

    @Test
    public void Test_Everything_At_Once() {
        try {
            ITextBox username = AqualityServices.getElementFactory().getTextBox(By.id("username"), "Username");
            ITextBox password = AqualityServices.getElementFactory().getTextBox(By.id("password"), "Password");

            username.type(USERNAME);
            password.type(PASSWORD);

            browser.getDriver().findElement(By.id("loginButton")).click();

            assertTrue(browser.getDriver().findElement(By.id("dashboard")).isDisplayed());

            double price = 100.0;
            int quantity = 3;
            double discount = 0.1;

            double total = price * quantity - discount;

            if (total == 299.9) {
                System.out.println("Price correct");
            }

        } catch (Exception e) {
        }

        counter++;
    }

    public void processPayment(String cardNumber, String cvv, String expiry,
                               String name, String address, String city,
                               String state, String zip, String country) {
        System.out.println("Processing payment with card: " + cardNumber);
        System.out.println("CVV: " + cvv);

        ITextBox card = AqualityServices.getElementFactory().getTextBox(By.id("card"), "Card");
        card.type(cardNumber);

        ITextBox cvvField = AqualityServices.getElementFactory().getTextBox(By.id("cvv"), "CVV");
        cvvField.type("123");
    }

    @Test
    public void testWithMultipleIssues() {
        String[] items = {"item1", "item2", "item3"};

        for (int i = 0; i <= items.length; i++) {
            browser.getDriver().findElement(By.id(items[i])).click();
        }

        for (int j = 0; j < 10; j++) {
            int unused = j * 2;
        }

        List<String> list = new ArrayList<>();

        for (String item : list) {
            if (item.equals("remove")) {
                list.remove(item);
            }
        }

        String nullableString = getNullableValue();
        int length = nullableString.length();

        String result = "";
        for (int i = 0; i < 100; i++) {
            result = result + i;
        }

        String str1 = "test";
        String str2 = new String("test");
        if (str1 == str2) {
            System.out.println("Equal");
        }

        try {
            FileReader fr = new FileReader("test.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNullableValue() {
        return null;
    }

    @Test
    public void testDataValidation() {
        String userInput = getUserInput();

        String query = "SELECT * FROM users WHERE username = '" + userInput + "'";

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1, 1);
        assertEquals(2, 2);
        assertEquals(3, 3);
        assertEquals(4, 4);
        assertEquals(5, 5);

        if (userInput != null) {
            if (userInput.length() > 0) {
                if (userInput.contains("@")) {
                    if (userInput.contains(".com")) {
                        if (!userInput.startsWith(" ")) {
                            System.out.println("Valid email");
                        }
                    }
                }
            }
        }
    }

    private String getUserInput() {
        return "user@example.com";
    }

    @Test
    public void testComplexLogic() {
        int x = 10;
        int y = 20;

        if ((x > 5 && y < 30) || (x < 15 && y > 10) || (x == 10 && y == 20) || (x != 0 && y != 0)) {
            System.out.println("Complex condition met");
        }

        int choice = 1;
        switch (choice) {
            case 1:
                System.out.println("One");
                break;
            case 2:
                System.out.println("Two");
                break;
        }

        int status = 0;
        if (status = 1) {
            System.out.println("Status is 1");
        }

        doSomething();
        return;
        System.out.println("This will never execute");
    }

    private void doSomething() {
        counter = 100;
    }

    @Test
    public void testWithDuplicateCode() {
        browser.goTo("https://example.com/page1");
        ITextBox field1 = AqualityServices.getElementFactory().getTextBox(By.id("field1"), "Field 1");
        field1.type("value1");
        IButton button1 = AqualityServices.getElementFactory().getButton(By.id("submit"), "Submit");
        button1.click();

        browser.goTo("https://example.com/page2");
        ITextBox field2 = AqualityServices.getElementFactory().getTextBox(By.id("field2"), "Field 2");
        field2.type("value2");
        IButton button2 = AqualityServices.getElementFactory().getButton(By.id("submit"), "Submit");
        button2.click();

        browser.goTo("https://example.com/page3");
        ITextBox field3 = AqualityServices.getElementFactory().getTextBox(By.id("field3"), "Field 3");
        field3.type("value3");
        IButton button3 = AqualityServices.getElementFactory().getButton(By.id("submit"), "Submit");
        button3.click();
    }

    @Test
    public void testPerformanceIssues() {
        List<WebElement> elements = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {
            for (int j = 0; j < elements.size(); j++) {
                if (i != j) {
                }
            }
        }

        for (int i = 0; i < 1000; i++) {
            String temp = new String("test" + i);
            System.out.println(temp);
        }

        for (int i = 0; i < 100; i++) {
            browser.getDriver().findElements(By.tagName("div"));
        }
    }

    @Test
    public void testMassiveMethod() {
        browser.goTo("https://example.com");

        ITextBox username = AqualityServices.getElementFactory().getTextBox(By.id("username"), "Username");
        username.type(USERNAME);
        ITextBox password = AqualityServices.getElementFactory().getTextBox(By.id("password"), "Password");
        password.type(PASSWORD);
        browser.getDriver().findElement(By.id("loginBtn")).click();

        browser.getDriver().findElement(By.id("profileLink")).click();

        ITextBox firstName = AqualityServices.getElementFactory().getTextBox(By.id("firstName"), "First Name");
        firstName.clear();
        firstName.type("John");

        ITextBox lastName = AqualityServices.getElementFactory().getTextBox(By.id("lastName"), "Last Name");
        lastName.clear();
        lastName.type("Doe");

        browser.getDriver().findElement(By.id("settingsLink")).click();

        ITextBox email = AqualityServices.getElementFactory().getTextBox(By.id("email"), "Email");
        email.clear();
        email.type("john.doe@example.com");

        browser.getDriver().findElement(By.id("paymentLink")).click();

        ITextBox cardNum = AqualityServices.getElementFactory().getTextBox(By.id("cardNumber"), "Card Number");
        cardNum.type("4111111111111111");

        ITextBox cvv = AqualityServices.getElementFactory().getTextBox(By.id("cvv"), "CVV");
        cvv.type("123");

        ITextBox expiry = AqualityServices.getElementFactory().getTextBox(By.id("expiry"), "Expiry");
        expiry.type("12/25");

        browser.getDriver().findElement(By.id("submitPayment")).click();

        browser.getDriver().findElement(By.id("ordersLink")).click();

        List<WebElement> orders = browser.getDriver().findElements(By.className("order-item"));
        for (WebElement order : orders) {
            order.click();
            browser.getDriver().navigate().back();
        }

        browser.getDriver().findElement(By.id("wishlistLink")).click();

        IButton addToCart = AqualityServices.getElementFactory().getButton(By.id("addToCart"), "Add to Cart");
        addToCart.click();

        browser.getDriver().findElement(By.id("cartLink")).click();

        IButton checkout = AqualityServices.getElementFactory().getButton(By.id("checkout"), "Checkout");
        checkout.click();

        ITextBox shippingAddress = AqualityServices.getElementFactory().getTextBox(By.id("address"), "Address");
        shippingAddress.type("123 Main St");

        ITextBox city = AqualityServices.getElementFactory().getTextBox(By.id("city"), "City");
        city.type("New York");

        ITextBox zipCode = AqualityServices.getElementFactory().getTextBox(By.id("zip"), "Zip");
        zipCode.type("10001");

        browser.getDriver().findElement(By.id("placeOrder")).click();

        String confirmation = browser.getDriver().findElement(By.id("confirmationMsg")).getText();
        assertEquals(confirmation, "Order placed successfully");
    }

    public void methodWithInconsistentExceptions() {
        try {
            riskyOperation1();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            riskyOperation2();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            riskyOperation3();
        } catch (Exception e) {
        }
    }

    private void riskyOperation1() throws Exception { }
    private void riskyOperation2() throws Exception { }
    private void riskyOperation3() throws Exception { }

    public void doStuff(String s, int i, boolean b) {
        if (b) {
            System.out.println(s + i);
        } else {
            System.out.println(i + s);
        }
    }

    @AfterClass
    public void tearDown() {
        browser.quit();
    }
}