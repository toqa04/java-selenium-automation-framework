package framework.waits;

import java.time.Duration;
import java.util.List;

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
import framework.exceptions.WaitException;

/**
 * Provides explicit wait functionality for WebDriver elements.
 *
 * Uses Selenium's WebDriverWait with transient exception handling for common
 * stale element and missing element conditions. It is designed for production
 * use and uses ConfigManager for timeout values.
 *
 * Thread-safe: creates a fresh WebDriverWait for each wait operation.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class ExplicitWait {

    private static final Logger logger = LoggerFactory.getLogger(ExplicitWait.class);
    private static final String DEFAULT_POLLING_INTERVAL_PROPERTY = "pollingInterval";
    private static final int DEFAULT_POLLING_INTERVAL_MILLIS = 250;

    private final WebDriver driver;
    private final Duration timeout;
    private final Duration pollingInterval;

    /**
     * Constructs an ExplicitWait using the active framework configuration.
     *
     * @param driver the WebDriver instance
     * @param configuration the framework configuration
     */
    public ExplicitWait(WebDriver driver, Configuration configuration) {
        this(driver, Duration.ofSeconds(configuration.getExplicitWait()),
                Duration.ofMillis(resolvePollingIntervalMillis()));
    }

    /**
     * Constructs an ExplicitWait with a custom timeout.
     *
     * @param driver the WebDriver instance
     * @param timeout the timeout duration
     */
    public ExplicitWait(WebDriver driver, Duration timeout) {
        this(driver, timeout, Duration.ofMillis(DEFAULT_POLLING_INTERVAL_MILLIS));
    }

    /**
     * Constructs an ExplicitWait with custom timeout and polling interval.
     *
     * @param driver the WebDriver instance
     * @param timeout the timeout duration
     * @param pollingInterval the polling interval duration
     */
    public ExplicitWait(WebDriver driver, Duration timeout, Duration pollingInterval) {
        this.driver = driver;
        this.timeout = timeout;
        this.pollingInterval = pollingInterval;
    }

    /**
     * Waits for an element to be visible.
     *
     * @param locator the locator
     * @return the visible element
     */
    public WebElement waitForVisibility(By locator) {
        return waitForExpectedCondition(
                ExpectedConditions.visibilityOfElementLocated(locator),
                "visibility",
                locator);
    }

    /**
     * Waits for an element to be clickable.
     *
     * @param locator the locator
     * @return the clickable element
     */
    public WebElement waitForClickability(By locator) {
        return waitForExpectedCondition(
                ExpectedConditions.elementToBeClickable(locator),
                "clickable",
                locator);
    }

    /**
     * Waits for an element to exist in the DOM.
     *
     * @param locator the locator
     * @return the element
     */
    public WebElement waitForPresence(By locator) {
        return waitForExpectedCondition(
                ExpectedConditions.presenceOfElementLocated(locator),
                "presence",
                locator);
    }

    /**
     * Waits for an element to disappear.
     *
     * @param locator the locator
     * @return true when the element is no longer visible or present
     */
    public boolean waitForInvisibility(By locator) {
        return waitForBooleanCondition(
                ExpectedConditions.invisibilityOfElementLocated(locator),
                "invisibility",
                locator);
    }

    /**
     * Waits for all matching elements to be present.
     *
     * @param locator the locator
     * @return the matching elements
     */
    public List<WebElement> waitForPresenceOfAll(By locator) {
        return waitForExpectedConditionList(
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator),
                "presence of all",
                locator);
    }

    /**
     * Waits for text to appear within an element.
     *
     * @param locator the locator
     * @param text the expected text
     * @return the element containing the text
     */
    public boolean waitForTextInElement(By locator, String text) {
        return waitForBooleanCondition(
                ExpectedConditions.textToBePresentInElementLocated(locator, text),
                "text='" + text + "'",
                locator);
    }

    /**
     * Waits for an attribute to match a value.
     *
     * @param locator the locator
     * @param attribute the attribute name
     * @param value the expected value
     * @return true when the attribute matches
     */
    public boolean waitForAttribute(By locator, String attribute, String value) {
        return waitForBooleanCondition(
                ExpectedConditions.attributeToBe(locator, attribute, value),
                "attribute " + attribute + "=" + value,
                locator);
    }

    /**
     * Waits until the URL contains the provided text.
     *
     * @param urlFragment the expected URL fragment
     * @return true when the URL matches
     */
    public boolean waitForUrlContains(String urlFragment) {
        return waitForBooleanCondition(
                ExpectedConditions.urlContains(urlFragment),
                "URL contains '" + urlFragment + "'",
                null);
    }

    /**
     * Waits until the page title contains the provided text.
     *
     * @param titleFragment the expected title fragment
     * @return true when the title matches
     */
    public boolean waitForTitleContains(String titleFragment) {
        return waitForBooleanCondition(
                ExpectedConditions.titleContains(titleFragment),
                "title contains '" + titleFragment + "'",
                null);
    }

    /**
     * Waits for a custom condition.
     *
     * @param strategy the custom wait strategy
     * @return the element matched by the strategy
     */
    public WebElement waitForCustom(WaitStrategy strategy) {
        WebDriverWait wait = newWebDriverWait();
        try {
            WebElement element = wait.until(currentDriver -> {
                try {
                    return strategy.execute(currentDriver);
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    logger.debug("Transient element issue ignored during wait: {}", e.getClass().getSimpleName());
                    return null;
                }
            });
            logger.debug("Custom wait strategy satisfied: timeout={}, pollingInterval={}", timeout, pollingInterval);
            return element;
        } catch (Exception e) {
            throw new WaitException(
                    "Custom wait failed; timeout=" + timeout + ", pollingInterval=" + pollingInterval
                    + ", expected condition=custom strategy",
                    e);
        }
    }

    private WebElement waitForExpectedCondition(ExpectedCondition<WebElement> condition, String expectedCondition, By locator) {
        WebDriverWait wait = newWebDriverWait();
        try {
            WebElement element = wait.until(currentDriver -> {
                try {
                    return condition.apply(currentDriver);
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    logger.debug("Transient wait issue ignored for {} on locator {}: {}",
                            expectedCondition, locator, e.getClass().getSimpleName());
                    return null;
                }
            });
            logger.debug("Explicit wait satisfied: condition={}, locator={}, timeout={}", expectedCondition, locator, timeout);
            return element;
        } catch (Exception e) {
            throw buildWaitException(expectedCondition, locator, e);
        }
    }

    private boolean waitForBooleanCondition(ExpectedCondition<Boolean> condition, String expectedCondition, By locator) {
        WebDriverWait wait = newWebDriverWait();
        try {
            boolean result = wait.until(currentDriver -> {
                try {
                    return condition.apply(currentDriver);
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    logger.debug("Transient wait issue ignored for {} on locator {}: {}",
                            expectedCondition, locator, e.getClass().getSimpleName());
                    return false;
                }
            });
            logger.debug("Explicit wait satisfied: condition={}, locator={}, timeout={}", expectedCondition, locator, timeout);
            return result;
        } catch (Exception e) {
            throw buildWaitException(expectedCondition, locator, e);
        }
    }

    private List<WebElement> waitForExpectedConditionList(ExpectedCondition<List<WebElement>> condition, String expectedCondition, By locator) {
        WebDriverWait wait = newWebDriverWait();
        try {
            List<WebElement> elements = wait.until(currentDriver -> {
                try {
                    return condition.apply(currentDriver);
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    logger.debug("Transient wait issue ignored for {} on locator {}: {}",
                            expectedCondition, locator, e.getClass().getSimpleName());
                    return null;
                }
            });
            logger.debug("Explicit wait satisfied: condition={}, locator={}, timeout={}", expectedCondition, locator, timeout);
            return elements;
        } catch (Exception e) {
            throw buildWaitException(expectedCondition, locator, e);
        }
    }

    private WebDriverWait newWebDriverWait() {
        return new WebDriverWait(driver, timeout, pollingInterval);
    }

    private WaitException buildWaitException(String expectedCondition, By locator, Exception cause) {
        String message = "Explicit wait failed; condition='" + expectedCondition + "', timeout=" + timeout
                + ", locator=" + (locator != null ? locator.toString() : "N/A")
                + ", pollingInterval=" + pollingInterval;
        logger.error(message, cause);
        return new WaitException(message, cause);
    }

    private static int resolvePollingIntervalMillis() {
        return resolvePollingIntervalMillis(ConfigManager.getInstance());
    }

    private static int resolvePollingIntervalMillis(ConfigManager configManager) {
        String value = configManager.getProperty(DEFAULT_POLLING_INTERVAL_PROPERTY,
                String.valueOf(DEFAULT_POLLING_INTERVAL_MILLIS));
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                logger.warn("Polling interval must be > 0; using default {} ms.", DEFAULT_POLLING_INTERVAL_MILLIS);
                return DEFAULT_POLLING_INTERVAL_MILLIS;
            }
            return parsed;
        } catch (NumberFormatException e) {
            logger.warn("Invalid polling interval '{}'; using default {} ms.", value, DEFAULT_POLLING_INTERVAL_MILLIS);
            return DEFAULT_POLLING_INTERVAL_MILLIS;
        }
    }
}
