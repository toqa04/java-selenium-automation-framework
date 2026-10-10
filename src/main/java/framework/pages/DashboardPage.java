package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import framework.config.Configuration;

/**
 * Page object for the OrangeHRM dashboard.
 */
public class DashboardPage extends BasePage {

    private static final By DASHBOARD_HEADING = By.xpath("//h6[normalize-space()='Dashboard']");

    public DashboardPage(WebDriver driver, Configuration configuration) {
        super(driver, configuration);
    }

    public boolean isDisplayed() {
        return isDisplayed(DASHBOARD_HEADING);
    }

    public String getHeading() {
        return getText(DASHBOARD_HEADING);
    }
}
