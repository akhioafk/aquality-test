package aquality.selenium.template.configuration;
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





// IM SOOOOO DONE WITH YOUUUUUU, YOU SON OF A FOREST!!!














// halllooo



