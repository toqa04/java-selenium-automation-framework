package framework.pages;

import framework.config.ConfigManager;
import framework.config.Configuration;
import framework.driver.DriverManager;
import framework.waits.WaitManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic reusable behavior for all page objects.
 *
 * This class intentionally contains only framework-level page actions and waits.
 * It does not include application-specific logic or test assertions.
 */
public abstract class BasePage {

    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected final WebDriver driver;
    protected final WaitManager waitManager;

    public BasePage(WebDriver driver) {
        this.driver = driver != null ? driver : DriverManager.getDriver();
        this.waitManager = new WaitManager(this.driver, ConfigManager.getInstance());
    }

    public BasePage(WebDriver driver, Configuration configuration) {
        this.driver = driver != null ? driver : DriverManager.getDriver();
        this.waitManager = configuration != null
                ? new WaitManager(this.driver, configuration)
                : new WaitManager(this.driver, ConfigManager.getInstance());
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
        logger.info("Navigated to URL: {}", url);
    }

    public void navigateBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }

    public void navigateForward() {
        driver.navigate().forward();
        logger.info("Navigated forward");
    }

    public void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }

    public void click(By locator) {
        waitManager.waitForClickable(locator).click();
        logger.debug("Clicked element: {}", locator);
    }

    public void type(By locator, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text value cannot be null");
        }
        WebElement element = waitManager.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed into element: {}", locator);
    }

    public void clear(By locator) {
        waitManager.waitForVisible(locator).clear();
        logger.debug("Cleared element: {}", locator);
    }

    public String getText(By locator) {
        String value = waitManager.waitForVisible(locator).getText();
        logger.debug("Retrieved text from element: {}", locator);
        return value;
    }

    public boolean isDisplayed(By locator) {
        try {
            return waitManager.waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed: {}", locator);
            return false;
        }
    }

    public boolean isEnabled(By locator) {
        try {
            return waitManager.waitForVisible(locator).isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", locator);
            return false;
        }
    }

    public String getAttribute(By locator, String attribute) {
        String value = waitManager.waitForVisible(locator).getAttribute(attribute);
        logger.debug("Retrieved attribute '{}' from element: {}", attribute, locator);
        return value;
    }

    public void scrollIntoView(By locator) {
        WebElement element = waitManager.waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        logger.debug("Scrolled element into view: {}", locator);
    }

    public WebElement waitForVisible(By locator) {
        return waitManager.waitForVisible(locator);
    }

    public WebElement waitForClickable(By locator) {
        return waitManager.waitForClickable(locator);
    }

    public WebElement waitForPresence(By locator) {
        return waitManager.waitForPresence(locator);
    }

    protected Object executeJavaScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected String getPageSource() {
        return driver.getPageSource();
    }
}
