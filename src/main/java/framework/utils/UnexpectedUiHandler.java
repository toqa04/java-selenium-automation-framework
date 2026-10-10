package framework.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Handles common unexpected UI states encountered during automation runs.
 *
 * This is intentionally generic: it catches unhandled alerts and common overlay/cookie banners
 * by scanning for standard dialog and banner patterns without hardcoding any application-specific selectors.
 */
public final class UnexpectedUiHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnexpectedUiHandler.class);
    private static final List<String> COMMON_DIALOG_SELECTORS = Arrays.asList(
            "[role='dialog']",
            ".modal",
            ".overlay",
            ".toast",
            ".notification",
            ".cookie-banner",
            ".alert-banner"
    );

    private UnexpectedUiHandler() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Generic handling for alert dialogs, overlays, and common dismissal banners.
     *
     * @param driver the active WebDriver instance
     * @return true when at least one unexpected UI element was handled, false otherwise
     */
    public static boolean handleUnexpectedUi(WebDriver driver) {
        if (driver == null) {
            return false;
        }

        boolean handled = false;

        try {
            Alert alert = driver.switchTo().alert();
            if (alert != null) {
                logger.warn("Unexpected alert detected; accepting it automatically.");
                alert.accept();
                handled = true;
            }
        } catch (NoAlertPresentException ignored) {
            // No unexpected alert is present.
        }

        for (String selector : COMMON_DIALOG_SELECTORS) {
            try {
                Object count = ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelectorAll(arguments[0]).length;",
                        selector
                );
                int matches = count instanceof Number ? ((Number) count).intValue() : 0;
                if (matches > 0) {
                    WebElement dismissButton = findDismissButton(driver, selector);
                    if (dismissButton != null) {
                        dismissButton.click();
                        logger.info("Closed unexpected UI banner using selector: {}", selector);
                        handled = true;
                    }
                }
            } catch (Exception ignored) {
                // Ignore selectors that are not present on the page.
            }
        }

        WebElement confirmationButton = findConfirmationButton(driver, "Yes");
        if (confirmationButton != null) {
            confirmationButton.click();
            logger.info("Accepted the confirmation dialog by clicking Yes.");
            handled = true;
        }

        return handled;
    }

    private static WebElement findDismissButton(WebDriver driver, String selector) {
        try {
            WebElement element = driver.findElement(By.cssSelector(selector));
            if (element != null && element.isDisplayed()) {
                return element;
            }
        } catch (Exception ignored) {
            // Fall back to common dismiss patterns.
        }

        List<String> dismissSelectors = Arrays.asList(
                "button[aria-label='Close']",
                "button[title='Close']",
                "button[data-dismiss]",
                "button[class*='close']",
                "button[class*='dismiss']",
                "button[onclick*='close']"
        );

        for (String dismissSelector : dismissSelectors) {
            try {
                WebElement button = driver.findElement(By.cssSelector(dismissSelector));
                if (button != null && button.isDisplayed()) {
                    return button;
                }
            } catch (Exception ignored) {
                // Try the next pattern.
            }
        }

        return null;
    }

    private static WebElement findConfirmationButton(WebDriver driver, String text) {
        List<String> candidates = Arrays.asList(
                "//button[normalize-space()=\'" + text + "\']",
                "//input[@value=\'" + text + "\']",
                "//*[normalize-space()=\'" + text + "\' and self::button or self::a]"
        );

        for (String candidate : candidates) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(candidate));
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        return element;
                    }
                }
            } catch (Exception ignored) {
                // Try the next locator.
            }
        }

        return null;
    }
}
