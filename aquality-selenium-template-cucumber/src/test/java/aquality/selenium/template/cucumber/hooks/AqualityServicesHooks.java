package aquality.selenium.template.cucumber.hooks;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.template.modules.CustomBrowserModule;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class AqualityServicesHooks {

    @Before(order = 0)
    public void reinitializeAqualityServices() {
        AqualityServices.initInjector(new CustomBrowserModule());
    }

    @After(order = 0)
    public void quitAqualityServices() {
        AqualityServices.quit();
    }
}
