package tests.base;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import framework.config.Configuration;
import framework.config.ConfigurationLoader;
import framework.driver.DriverManager;
import framework.driver.DriverOptions;
import framework.screenshots.ScreenshotManager;

/**
 * Base test lifecycle for all TestNG tests.
 *
 * This class is intentionally generic and does not contain application-specific
 * logic. It creates the driver, configures the browser, captures screenshots on
 * failure, and cleans up the thread-local driver after every test.
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected Configuration configuration;
    protected ScreenshotManager screenshotManager;

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestContext testContext) {
        try {
            logger.info("Preparing test environment: {}", testContext.getCurrentXmlTest().getName());
            configuration = ConfigurationLoader.load();
            DriverManager.initializeDriver(configuration);
            prepareBrowser();
            screenshotManager = new ScreenshotManager(configuration.getReportsDirectory());
            beforeTestSetup(testContext);
            logger.info("Browser ready for test execution");
        } catch (Exception e) {
            logger.error("Test setup failed", e);
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            boolean failed = result.getStatus() == ITestResult.FAILURE;
            boolean captureScreenshot = configuration != null
                    && (configuration.isScreenshotAlways()
                    || (failed && configuration.isScreenshotOnFailure()));
            if (captureScreenshot) {
                captureFailureScreenshot(result);
            }
            afterTestTeardown(result);
        } catch (Exception e) {
            logger.warn("Test teardown hook had an issue; original test result will still be preserved.", e);
        } finally {
            try {
                if (DriverManager.isDriverInitialized()) {
                    DriverManager.quitDriver();
                }
            } catch (Exception e) {
                logger.error("Driver cleanup failed during teardown", e);
            }
            screenshotManager = null;
            logger.info("Test cleanup completed for: {}", result.getMethod().getMethodName());
        }
    }

    protected void beforeTestSetup(ITestContext testContext) {
        // Override in subclasses if needed.
    }

    protected void afterTestTeardown(ITestResult result) {
        // Override in subclasses if needed.
    }

    protected void prepareBrowser() {
        WebDriver driver = getDriver();
        DriverOptions options = DriverManager.getDriverOptions();

        if (options.isMaximized()) {
            driver.manage().window().maximize();
        }

        driver.manage().deleteAllCookies();
        logger.debug("Browser prepared for thread: {}", Thread.currentThread().getName());
    }

    protected void captureFailureScreenshot(ITestResult result) {
        if (screenshotManager == null || !DriverManager.isDriverInitialized()) {
            logger.debug("Screenshot capture skipped because no driver or manager is available.");
            return;
        }

        try {
            String testName = result.getMethod().getMethodName();
            waitForPageBeforeScreenshot();
            String screenshotPath = screenshotManager.captureScreenshot(testName);
            if (screenshotPath != null) {
                result.setAttribute("screenshotPath", screenshotPath);
                logger.info("Screenshot captured for '{}': {}", testName, screenshotPath);
            }
        } catch (Exception e) {
            logger.warn("Screenshot capture failed; original test failure will not be hidden.", e);
        }
    }

    private void waitForPageBeforeScreenshot() {
        WebDriver driver = getDriver();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(currentDriver
                    -> "complete".equals(((JavascriptExecutor) currentDriver)
                            .executeScript("return document.readyState")));
        } catch (Exception e) {
            logger.debug("Page did not report readyState=complete before screenshot.", e);
        }

        int delaySeconds = configuration == null ? 3 : configuration.getScreenshotDelaySeconds();
        if (delaySeconds > 0) {
            try {
                logger.info("Waiting {} seconds before capturing screenshot.", delaySeconds);
                Thread.sleep(Duration.ofSeconds(delaySeconds).toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.debug("Screenshot delay interrupted.", e);
            }
        }
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    protected String getTestUsername() {
        return getCredential("test.username", "ORANGEHRM_TEST_USERNAME", "Admin");
    }

    protected String getTestPassword() {
        return getCredential("test.password", "ORANGEHRM_TEST_PASSWORD", null);
    }

    private String getCredential(String propertyName, String environmentVariable, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        if (value == null || value.isBlank()) {
            value = configuration.getProperty(propertyName);
        }
        if ((value == null || value.isBlank()) && defaultValue != null) {
            value = defaultValue;
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Set " + environmentVariable
                    + " or -D" + propertyName + " to provide the test credential.");
        }
        return value;
    }

    protected ScreenshotManager getScreenshotManager() {
        return screenshotManager;
    }

    protected String getDriverInfo() {
        return DriverManager.getDriverInfo();
    }
}
