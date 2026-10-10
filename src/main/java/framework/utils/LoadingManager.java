package framework.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Generic page-loading and readiness utilities used across the framework.
 *
 * This class centralizes the common browser checks needed before interaction:
 * - document.readyState == "complete"
 * - jQuery active requests are settled when jQuery is present
 * - readiness checks fail gracefully when no driver is available
 */
public final class LoadingManager {

    private static final Logger logger = LoggerFactory.getLogger(LoadingManager.class);

    private LoadingManager() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Checks whether the current document has finished loading.
     *
     * @param driver the current WebDriver instance
     * @return true when the document is ready, false when the driver is null or the check fails
     */
    public static boolean isPageLoaded(WebDriver driver) {
        if (driver == null) {
            return false;
        }

        try {
            Object readyState = ((JavascriptExecutor) driver).executeScript("return document.readyState");
            return "complete".equalsIgnoreCase(String.valueOf(readyState));
        } catch (Exception e) {
            logger.debug("Page-ready check failed because the page is not available or the driver is inactive.", e);
            return false;
        }
    }

    /**
     * Waits until the page is ready and any jQuery activity has settled.
     *
     * @param driver the current WebDriver instance
     * @param timeout the maximum time to wait
     * @return true when the page becomes ready before the timeout, false otherwise
     */
    public static boolean waitForPageReady(WebDriver driver, Duration timeout) {
        if (driver == null || timeout == null || timeout.isNegative()) {
            return false;
        }

        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (isPageLoaded(driver) && isAjaxIdle(driver)) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Page-loading wait interrupted.", e);
                return false;
            }
        }

        return isPageLoaded(driver) && isAjaxIdle(driver);
    }

    /**
     * Waits for the page to finish loading using a default timeout of 10 seconds.
     *
     * @param driver the current WebDriver instance
     * @return true when the page becomes ready within the default timeout
     */
    public static boolean waitForPageReady(WebDriver driver) {
        return waitForPageReady(driver, Duration.ofSeconds(10));
    }

    /**
     * Waits for a page load to complete using a custom timeout.
     *
     * @param driver the current WebDriver instance
     * @param timeoutSeconds maximum timeout in seconds
     * @return true if the page reaches ready state within the timeout
     */
    public static boolean waitForPageLoad(WebDriver driver, long timeoutSeconds) {
        return waitForPageReady(driver, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Checks whether the document is idle, accounting for jQuery activity.
     *
     * @param driver the current WebDriver instance
     * @return true when no active request is detected or jQuery is unavailable
     */
    public static boolean isAjaxIdle(WebDriver driver) {
        if (driver == null) {
            return false;
        }

        try {
            Object inactive = ((JavascriptExecutor) driver).executeScript(
                    "return (typeof window.jQuery === 'undefined') ? true : (window.jQuery.active === 0);"
            );
            return Boolean.TRUE.equals(inactive);
        } catch (Exception e) {
            logger.debug("AJAX idle check skipped because the page does not expose jQuery or is not ready.", e);
            return true;
        }
    }
}
