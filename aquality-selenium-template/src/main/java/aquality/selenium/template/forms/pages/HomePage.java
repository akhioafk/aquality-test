package aquality.selenium.template.forms.pages;

import aquality.selenium.template.forms.BaseAppForm;
import org.openqa.selenium.By;

public class HomePage extends BaseAppForm {

    public MainPage() {
        super(By.xpath("//section[contains(@class, 'services')]"), "Main page");
    }
}
