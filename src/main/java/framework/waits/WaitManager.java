package framework.waits;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.config.ConfigManager;
import framework.config.Configuration;
import framework.driver.DriverManager;
import framework.exceptions.WaitException;

/**
 * Centralized wait manager for the framework.
 *
 * This class is the single entry point for all explicit wait logic. It uses
 * WebDriverWait with a timeout sourced from ConfigManager and a configurable
 * polling interval. It obtains the WebDriver through DriverManager rather than
 * storing it in a static field.
 *
 * All waits use explicit waits only; Thread.sleep() is forbidden.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class WaitManager {

    private static final Logger logger = LoggerFactory.getLogger(WaitManager.class);
    private static final String DEFAULT_POLLING_INTERVAL_PROPERTY = "pollingInterval";
    private static final int DEFAULT_POLLING_INTERVAL_MILLIS = 250;

    private final WebDriver driver;
    private final Duration timeout;
    private final Duration pollingInterval;

    /**
     * Constructs a WaitManager that resolves the current thread's WebDriver
     * from DriverManager.
     */
    public WaitManager() {
        this(DriverManager.getDriver(), ConfigManager.getInstance());
    }

    /**
     * Constructs a WaitManager with a specific WebDriver.
     *
     * @param driver the WebDriver instance
     */
    public WaitManager(WebDriver driver) {
        this(driver, ConfigManager.getInstance());
    }

    /**
     * Constructs a WaitManager using framework configuration.
     *
     * @param driver the WebDriver instance
     * @param configuration the framework configuration
     */
    public WaitManager(WebDriver driver, Configuration configuration) {
        this.driver = driver != null ? driver : DriverManager.getDriver();
        this.timeout = Duration.ofSeconds(configuration.getExplicitWait());
        this.pollingInterval = Duration.ofMillis(resolvePollingIntervalMillis());
    }

    /**
     * Constructs a WaitManager using the active ConfigManager values.
     *
     * @param driver the WebDriver instance
     * @param configManager the active configuration manager instance
     */
    public WaitManager(WebDriver driver, ConfigManager configManager) {
        this.driver = driver != null ? driver : DriverManager.getDriver();
        this.timeout = Duration.ofSeconds(configManager.getExplicitWait());
        this.pollingInterval = Duration.ofMillis(resolvePollingIntervalMillis(configManager));
    }

    /**
     * Constructs a WaitManager with a custom timeout and polling interval.
     *
     * @param driver the WebDriver instance
     * @param timeout the timeout duration
     * @param pollingInterval the polling interval
     */
    public WaitManager(WebDriver driver, Duration timeout, Duration pollingInterval) {
        this.driver = driver != null ? driver : DriverManager.getDriver();
        this.timeout = timeout;
        this.pollingInterval = pollingInterval;
    }

    /**
     * Waits until an element is visible.
     *
     * @param locator the element locator
     * @return the visible WebElement
     */
    public WebElement waitForVisible(By locator) {
        return waitForExpectedCondition(
                ExpectedConditions.visibilityOfElementLocated(locator),
                "visibility",
                locator);
    }

    /**
     * Alias kept for backward compatibility with the older framework naming.
     *
     * @param locator the element locator
     * @return the visible WebElement
     */
    public WebElement waitForVisibility(By locator) {
        return waitForVisible(locator);
    }

    /**
     * Waits until an element is present in the DOM.
     *
     * @param locator the element locator
     * @return the WebElement
     */
    public WebElement waitForPresence(By locator) {
        return waitForExpectedCondition(
                ExpectedConditions.presenceOfElementLocated(locator),
                "presence",
                locator);
    }

    /**
     * Waits until an element is clickable.
     *
     * @param locator the element locator
     * @return the clickable WebElement
     */
    public WebElement waitForClickable(By locator) {
        return waitForExpectedCondition(
                ExpectedConditions.elementToBeClickable(locator),
                "clickable",
                locator);
    }

    /**
     * Alias kept for compatibility with the older framework naming.
     *
     * @param locator the element locator
     * @return the clickable WebElement
     */
    public WebElement waitForClickability(By locator) {
        return waitForClickable(locator);
    }

    /**
     * Waits until an element is invisible or disappears.
     *
     * @param locator the element locator
     * @return true if the element becomes invisible
     */
    public boolean waitForInvisible(By locator) {
        return waitForBooleanCondition(
                ExpectedConditions.invisibilityOfElementLocated(locator),
                "invisibility",
                locator);
    }

    /**
     * Alias kept for compatibility with the older framework naming.
     *
     * @param locator the element locator
     * @return true if the element becomes invisible
     */
    public boolean waitForInvisibility(By locator) {
        return waitForInvisible(locator);
    }

    /**
     * Waits until text appears in the target element.
     *
     * @param locator the element locator
     * @param text the expected text
     * @return the WebElement containing the text
     */
    public boolean waitForText(By locator, String text) {
        return waitForBooleanCondition(
                ExpectedConditions.textToBePresentInElementLocated(locator, text),
                "text='" + text + "'",
                locator);
    }

    /**
     * Alias kept for compatibility with the older framework naming.
     *
     * @param locator the element locator
     * @param text the expected text
     * @return true when the text is present in the element
     */
    public boolean waitForTextInElement(By locator, String text) {
        return waitForText(locator, text);
    }

    /**
     * Waits until an attribute contains or matches the expected value.
     *
     * @param locator the element locator
     * @param attribute the attribute name
     * @param value the expected attribute value
     * @return true if the attribute condition is satisfied
     */
    public boolean waitForAttribute(By locator, String attribute, String value) {
        return waitForBooleanCondition(
                ExpectedConditions.attributeToBe(locator, attribute, value),
                "attribute " + attribute + "=" + value,
                locator);
    }

    /**
     * Waits until the current URL contains the expected text.
     *
     * @param url the expected URL fragment
     * @return true when the URL contains the fragment
     */
    public boolean waitForUrlContains(String url) {
        return waitForBooleanCondition(
                ExpectedConditions.urlContains(url),
                "URL contains '" + url + "'",
                null);
    }

    /**
     * Waits until the page title contains the expected text.
     *
     * @param title the expected title fragment
     * @return true when the title contains the fragment
     */
    public boolean waitForTitleContains(String title) {
        return waitForBooleanCondition(
                ExpectedConditions.titleContains(title),
                "title contains '" + title + "'",
                null);
    }

    /**
     * Waits for a custom Selenium expected condition.
     *
     * @param condition the expected condition
     * @param description text describing the condition
     * @param locator the locator involved, or null for URL/title-based
     * conditions
     * @param <T> expected return type
     * @return the condition result
     */
    public <T> T waitForCondition(ExpectedCondition<T> condition, String description, By locator) {
        WebDriverWait wait = newWebDriverWait();
        try {
            T result = wait.until(condition);
            logger.debug("Wait condition satisfied: {} | locator: {} | timeout: {}", description, locator, timeout);
            return result;
        } catch (Exception e) {
            throw buildWaitException(description, locator, e);
        }
    }

    /**
     * Waits until a custom condition returns a WebElement.
     *
     * @param strategy custom wait strategy
     * @return the WebElement when the condition is satisfied
     */
    public WebElement waitForCustom(WaitStrategy strategy) {
        try {
            WebDriverWait wait = newWebDriverWait();
            WebElement element = wait.until(currentDriver -> {
                try {
                    return strategy.execute(currentDriver);
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    logger.debug("Transient wait condition failure ignored: {}", e.getClass().getSimpleName());
                    return null;
                }
            });
            logger.debug("Custom wait strategy satisfied using timeout: {}", timeout);
            return element;
        } catch (Exception e) {
            throw new WaitException("Custom wait strategy failed; timeout=" + timeout + "; expected condition=custom strategy", e);
        }
    }

    /**
     * Gets the underlying explicit wait configuration.
     *
     * @return the configured timeout
     */
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * Gets the polling interval used for explicit waits.
     *
     * @return the polling interval duration
     */
    public Duration getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Backward-compatible convenience method for older page-object code.
     *
     * @return a new ExplicitWait instance bound to the current driver and
     * timeout
     */
    public ExplicitWait getExplicitWait() {
        return new ExplicitWait(driver, timeout, pollingInterval);
    }

    private WebElement waitForExpectedCondition(ExpectedCondition<WebElement> condition, String expectedCondition, By locator) {
        WebDriverWait wait = newWebDriverWait();
        try {
            WebElement element = wait.until(condition);
            logger.debug("Explicit wait satisfied: {} | locator: {} | timeout: {}", expectedCondition, locator, timeout);
            return element;
        } catch (Exception e) {
            throw buildWaitException(expectedCondition, locator, e);
        }
    }

    private boolean waitForBooleanCondition(ExpectedCondition<Boolean> condition, String expectedCondition, By locator) {
        WebDriverWait wait = newWebDriverWait();
        try {
            boolean result = wait.until(condition);
            logger.debug("Explicit wait satisfied: {} | locator: {} | timeout: {}", expectedCondition, locator, timeout);
            return result;
        } catch (Exception e) {
            throw buildWaitException(expectedCondition, locator, e);
        }
    }

    private WebDriverWait newWebDriverWait() {
        return new WebDriverWait(driver, timeout, pollingInterval);
    }

    private WaitException buildWaitException(String description, By locator, Exception cause) {
        String baseMessage = "Explicit wait failed: condition='" + description + "'"
                + ", timeout=" + timeout
                + ", locator=" + (locator != null ? locator.toString() : "N/A")
                + ", driver=" + (driver != null ? driver.getClass().getSimpleName() : "N/A");

        logger.error(baseMessage, cause);
        return new WaitException(baseMessage, cause);
    }

    private static int resolvePollingIntervalMillis() {
        return resolvePollingIntervalMillis(ConfigManager.getInstance());
    }

    private static int resolvePollingIntervalMillis(ConfigManager configManager) {
        String value = configManager.getProperty(DEFAULT_POLLING_INTERVAL_PROPERTY, String.valueOf(DEFAULT_POLLING_INTERVAL_MILLIS));
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                return DEFAULT_POLLING_INTERVAL_MILLIS;
            }
            return parsed;
        } catch (NumberFormatException e) {
            logger.warn("Invalid polling interval '{}'. Falling back to default {} ms.", value, DEFAULT_POLLING_INTERVAL_MILLIS);
            return DEFAULT_POLLING_INTERVAL_MILLIS;
        }
    }
}
