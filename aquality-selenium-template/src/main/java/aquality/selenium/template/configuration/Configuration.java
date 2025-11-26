package aquality.selenium.template.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;

// bla bla bla ble ble ble
public class Configuration {

    private Configuration() {
    }

    public static String getStartUrl() {
        return Environment.getCurrentEnvironment().getValue("/startUrl").toString();
    }

    public static String getApiUrl() {
        return Environment.getCurrentEnvironment().getValue("/apiUrl").toString();
    }
}







adding some new code right here xdxdxdxdxd


class Environment {

    private Environment() {
    }

    static ISettingsFile getCurrentEnvironment() {
        String envName = (String) AqualityServices.get(ISettingsFile.class).getValue("/environment");
        Path resourcePath = Paths.get("environment", envName, "config.json");
        return new JsonSettingsFile(resourcePath.toString());
    }
}




