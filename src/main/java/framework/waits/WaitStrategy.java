package framework.waits;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Strategy interface for WebDriver wait conditions.
 * 
 * Implements the Strategy design pattern to define various wait behaviors.
 * Different wait strategies can be plugged in without modifying client code.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
@FunctionalInterface
public interface WaitStrategy {
    
    /**
     * Executes the wait strategy.
     *
     * @param driver the WebDriver instance
     * @return a WebElement if the wait condition is met, null otherwise
     */
    WebElement execute(WebDriver driver);
}
