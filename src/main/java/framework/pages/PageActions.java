package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Advanced page interaction utilities.
 * 
 * Provides complex interactions like:
 * - Keyboard and mouse actions
 * - Dropdown/Select operations
 * - Drag and drop
 * - Double-click, right-click
 * - Key combinations
 * 
 * Utility class - provides static methods for use in page objects.
 * Not required to extend BasePage for these utilities.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class PageActions {
    
    private static final Logger logger = LoggerFactory.getLogger(PageActions.class);

    /**
     * Performs a double-click on an element.
     *
     * @param driver the WebDriver instance
     * @param element the WebElement to double-click
     */
    public static void doubleClick(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.doubleClick(element).perform();
        logger.debug("Double-clicked on element");
    }

    /**
     * Performs a right-click (context click) on an element.
     *
     * @param driver the WebDriver instance
     * @param element the WebElement to right-click
     */
    public static void rightClick(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
        logger.debug("Right-clicked on element");
    }

    /**
     * Hovers over an element (move mouse to element).
     *
     * @param driver the WebDriver instance
     * @param element the WebElement to hover over
     */
    public static void hoverOver(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
        logger.debug("Hovered over element");
    }

    /**
     * Drags an element to a target location.
     *
     * @param driver the WebDriver instance
     * @param sourceElement the element to drag
     * @param targetElement the target element or location
     */
    public static void dragAndDrop(WebDriver driver, WebElement sourceElement, WebElement targetElement) {
        Actions actions = new Actions(driver);
        actions.dragAndDrop(sourceElement, targetElement).perform();
        logger.debug("Dragged and dropped element");
    }

    /**
     * Selects an option from a dropdown by visible text.
     *
     * @param element the select element
     * @param visibleText the visible text of the option
     */
    public static void selectByVisibleText(WebElement element, String visibleText) {
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
        logger.debug("Selected option by visible text: {}", visibleText);
    }

    /**
     * Selects an option from a dropdown by value attribute.
     *
     * @param element the select element
     * @param value the value attribute of the option
     */
    public static void selectByValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
        logger.debug("Selected option by value: {}", value);
    }

    /**
     * Selects an option from a dropdown by index.
     *
     * @param element the select element
     * @param index the index of the option (0-based)
     */
    public static void selectByIndex(WebElement element, int index) {
        Select select = new Select(element);
        select.selectByIndex(index);
        logger.debug("Selected option by index: {}", index);
    }

    /**
     * Gets all options from a dropdown as text strings.
     *
     * @param element the select element
     * @return list of option texts
     */
    public static List<String> getAllOptionsText(WebElement element) {
        Select select = new Select(element);
        List<String> options = select.getOptions()
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
        logger.debug("Retrieved {} options from dropdown", options.size());
        return options;
    }

    /**
     * Gets the currently selected option from a dropdown.
     *
     * @param element the select element
     * @return the selected option text
     */
    public static String getSelectedOption(WebElement element) {
        Select select = new Select(element);
        String selectedText = select.getFirstSelectedOption().getText();
        logger.debug("Current selected option: {}", selectedText);
        return selectedText;
    }

    /**
     * Sends keyboard keys to an element.
     * Use org.openqa.selenium.Keys for key constants.
     *
     * @param element the WebElement
     * @param keys the keys to send
     */
    public static void sendSpecialKeys(WebElement element, CharSequence... keys) {
        element.sendKeys(keys);
        logger.debug("Sent special keys to element");
    }

    /**
     * Checks if a checkbox is checked.
     *
     * @param element the checkbox element
     * @return true if checked, false otherwise
     */
    public static boolean isCheckboxChecked(WebElement element) {
        boolean isChecked = element.isSelected();
        logger.debug("Checkbox checked: {}", isChecked);
        return isChecked;
    }

    /**
     * Sets checkbox state (check or uncheck).
     *
     * @param element the checkbox element
     * @param shouldBeChecked true to check, false to uncheck
     */
    public static void setCheckboxState(WebElement element, boolean shouldBeChecked) {
        boolean isCurrentlyChecked = element.isSelected();
        if (isCurrentlyChecked != shouldBeChecked) {
            element.click();
            logger.debug("Checkbox state set to: {}", shouldBeChecked);
        }
    }

    /**
     * Checks if an element is enabled.
     *
     * @param element the WebElement
     * @return true if element is enabled, false if disabled
     */
    public static boolean isElementEnabled(WebElement element) {
        boolean isEnabled = element.isEnabled();
        logger.debug("Element enabled: {}", isEnabled);
        return isEnabled;
    }

    /**
     * Gets all text from multiple elements.
     *
     * @param driver the WebDriver instance
     * @param locator the elements locator
     * @return list of text strings
     */
    public static List<String> getMultipleElementsText(WebDriver driver, By locator) {
        List<WebElement> elements = driver.findElements(locator);
        List<String> texts = elements.stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
        logger.debug("Retrieved text from {} elements", texts.size());
        return texts;
    }
}
