package framework.driver;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.exceptions.DriverException;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Factory for creating WebDriver instances.
 *
 * Implements the Factory design pattern to centralize WebDriver creation logic.
 * Supports multiple browsers with automatic driver management via
 * WebDriverManager.
 *
 * Thread-safe: Each call creates a new driver instance without shared state.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class WebDriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

    /**
     * Creates a WebDriver instance based on the provided options.
     *
     * @param options the driver configuration options
     * @return configured WebDriver instance
     * @throws DriverException if driver creation fails
     */
    public static WebDriver createDriver(DriverOptions options) {
        try {
            logger.info("Creating WebDriver for browser: {}", options.getBrowserType().getDisplayName());

            WebDriver driver;
            switch (options.getBrowserType()) {
                case CHROME:
                    driver = createChromeDriver(options);
                    break;
                case FIREFOX:
                    driver = createFirefoxDriver(options);
                    break;
                case SAFARI:
                    driver = createSafariDriver(options);
                    break;
                case EDGE:
                    driver = createEdgeDriver(options);
                    break;
                default:
                    throw new DriverException("Unsupported browser type: " + options.getBrowserType());
            }

            configureTimeouts(driver, options);
            logger.info("WebDriver created successfully: {}", options.getBrowserType().getDisplayName());
            return driver;

        } catch (RuntimeException e) {
            logger.error("Failed to create WebDriver for browser: {}", options.getBrowserType().getDisplayName(), e);
            throw new DriverException("Failed to create WebDriver: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a Chrome WebDriver instance. Uses WebDriverManager for automatic
     * driver download and management.
     *
     * @param options the driver options
     * @return Chrome WebDriver
     */
    private static WebDriver createChromeDriver(DriverOptions options) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        File chromeExecutable = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        if (chromeExecutable.isFile()) {
            chromeOptions.setBinary(chromeExecutable);
        }

        if (options.isHeadless()) {
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--window-size=1920,1080");
            logger.debug("Chrome headless mode enabled");
        }

        if (options.isMaximized() && !options.isHeadless()) {
            chromeOptions.addArguments("--start-maximized");
            logger.debug("Chrome maximize enabled");
        }

        // Additional Chrome options for stability
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-extensions");
        File profileDirectory = new File(System.getProperty("java.io.tmpdir"),
                "automation-chrome-" + UUID.randomUUID());
        chromeOptions.addArguments("--user-data-dir=" + profileDirectory.getAbsolutePath());

        return new ChromeDriver(chromeOptions);
    }

    /**
     * Creates a Firefox WebDriver instance. Uses WebDriverManager for automatic
     * driver download and management.
     *
     * @param options the driver options
     * @return Firefox WebDriver
     */
    private static WebDriver createFirefoxDriver(DriverOptions options) {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions firefoxOptions = new FirefoxOptions();

        if (options.isHeadless()) {
            firefoxOptions.addArguments("--headless");
            logger.debug("Firefox headless mode enabled");
        }

        if (options.isMaximized()) {
            firefoxOptions.addArguments("--start-maximized");
            logger.debug("Firefox maximize enabled");
        }

        return new FirefoxDriver(firefoxOptions);
    }

    /**
     * Creates a Safari WebDriver instance. Note: Safari driver has
     * platform-specific requirements.
     *
     * @param options the driver options
     * @return Safari WebDriver
     */
    private static WebDriver createSafariDriver(DriverOptions options) {
        logger.debug("Creating Safari driver (platform-specific)");

        if (options.isMaximized()) {
            logger.warn("Maximize option not applicable for Safari");
        }

        return new SafariDriver();
    }

    /**
     * Creates an Edge WebDriver instance. Uses WebDriverManager for automatic
     * driver download and management.
     *
     * @param options the driver options
     * @return Edge WebDriver
     */
    private static WebDriver createEdgeDriver(DriverOptions options) {
        WebDriverManager.edgedriver().setup();

        EdgeOptions edgeOptions = new EdgeOptions();

        if (options.isHeadless()) {
            edgeOptions.addArguments("--headless");
            logger.debug("Edge headless mode enabled");
        }

        if (options.isMaximized()) {
            edgeOptions.addArguments("--start-maximized");
            logger.debug("Edge maximize enabled");
        }

        // Additional Edge options for stability (similar to Chrome)
        edgeOptions.addArguments("--no-sandbox");
        edgeOptions.addArguments("--disable-dev-shm-usage");

        return new EdgeDriver(edgeOptions);
    }

    /**
     * Configures all timeouts for the WebDriver instance.
     *
     * @param driver the WebDriver to configure
     * @param options the driver options containing timeout values
     */
    private static void configureTimeouts(WebDriver driver, DriverOptions options) {
        try {
            if (options.getImplicitWaitSeconds() > 0) {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(options.getImplicitWaitSeconds()));
                logger.debug("Implicit wait configured: {} seconds", options.getImplicitWaitSeconds());
            }

            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(options.getPageLoadTimeoutSeconds()));
            logger.debug("Page load timeout configured: {} seconds", options.getPageLoadTimeoutSeconds());

            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(options.getScriptTimeoutSeconds()));
            logger.debug("Script timeout configured: {} seconds", options.getScriptTimeoutSeconds());

        } catch (RuntimeException e) {
            logger.warn("Failed to configure some timeouts", e);
        }
    }
}
