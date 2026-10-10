package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import framework.config.Configuration;

/**
 * OrangeHRM login screen.
 */
public class LoginPage extends BasePage {

    private final Configuration configuration;
    private static final By USERNAME_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver, Configuration configuration) {
        super(driver, configuration);
        this.configuration = configuration;
    }

    public void navigate() {
        String url = configuration != null ? configuration.getBaseUrl() : "";
        driver.get(url == null || url.trim().isEmpty()
                ? "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login" : url);
        logger.info("Navigated to OrangeHRM login page: {}", url);
    }

    public void login(String username, String password) {
        type(USERNAME_INPUT, username);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
    }
}
