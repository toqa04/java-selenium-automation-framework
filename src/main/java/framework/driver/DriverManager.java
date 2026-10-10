package framework.driver;

import framework.config.Configuration;
import framework.exceptions.DriverException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages WebDriver lifecycle and thread-local storage.
 * 
 * Implements thread-safe WebDriver management using ThreadLocal variables.
 * Each thread gets its own driver instance, enabling parallel test execution.
 * 
 * This class is the primary interface for accessing WebDriver instances
 * throughout the framework. It handles driver initialization, storage, and cleanup.
 * 
 * Design Pattern: Singleton per thread (ThreadLocal)
 * 
 * Thread-Safe: Yes, through ThreadLocal storage
 * Parallel Execution: Yes, each thread maintains independent driver
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class DriverManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<DriverOptions> optionsThreadLocal = new ThreadLocal<>();

    /**
     * Initializes the WebDriver for the current thread.
     * 
     * Creates and stores a new WebDriver instance in ThreadLocal storage.
     * If a driver already exists for this thread, logs a warning but still creates a new instance.
     *
     * @param configuration the framework configuration
     * @throws DriverException if driver initialization fails
     */
    public static void initializeDriver(Configuration configuration) {
        try {
            if (driverThreadLocal.get() != null) {
                logger.warn("Driver already initialized for thread: {}. Reinitializing.", 
                           Thread.currentThread().getName());
                quitDriver();
            }
            
            DriverOptions options = DriverOptions.fromConfiguration(configuration);
            optionsThreadLocal.set(options);
            
            WebDriver driver = WebDriverFactory.createDriver(options);
            driverThreadLocal.set(driver);
            
            logger.info("Driver initialized for thread: {} ({})", 
                       Thread.currentThread().getName(), 
                       options.getBrowserType().getDisplayName());
            
        } catch (Exception e) {
            logger.error("Failed to initialize driver for thread: {}", Thread.currentThread().getName(), e);
            throw new DriverException("Driver initialization failed", e);
        }
    }

    /**
     * Initializes the WebDriver with specific options.
     * 
     * Useful for testing specific browser configurations or
     * overriding configuration settings for individual tests.
     *
     * @param options the driver options to use
     * @throws DriverException if driver initialization fails
     */
    public static void initializeDriver(DriverOptions options) {
        try {
            if (driverThreadLocal.get() != null) {
                logger.warn("Driver already initialized for thread: {}. Reinitializing.", 
                           Thread.currentThread().getName());
                quitDriver();
            }
            
            optionsThreadLocal.set(options);
            WebDriver driver = WebDriverFactory.createDriver(options);
            driverThreadLocal.set(driver);
            
            logger.info("Driver initialized for thread: {} ({})", 
                       Thread.currentThread().getName(), 
                       options.getBrowserType().getDisplayName());
            
        } catch (Exception e) {
            logger.error("Failed to initialize driver for thread: {}", Thread.currentThread().getName(), e);
            throw new DriverException("Driver initialization failed", e);
        }
    }

    /**
     * Gets the WebDriver instance for the current thread.
     *
     * @return the WebDriver instance
     * @throws DriverException if no driver is initialized for this thread
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new DriverException("WebDriver not initialized for thread: " + 
                                     Thread.currentThread().getName() + 
                                     ". Call initializeDriver() first.");
        }
        return driver;
    }

    /**
     * Gets the driver options for the current thread.
     *
     * @return the driver options used to initialize this driver
     * @throws DriverException if no driver is initialized for this thread
     */
    public static DriverOptions getDriverOptions() {
        DriverOptions options = optionsThreadLocal.get();
        if (options == null) {
            throw new DriverException("Driver options not available for thread: " + 
                                     Thread.currentThread().getName());
        }
        return options;
    }

    /**
     * Checks if a driver is initialized for the current thread.
     *
     * @return true if a driver exists, false otherwise
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }

    /**
     * Quits the WebDriver and cleans up thread-local storage.
     * 
     * Safe to call even if no driver is initialized.
     * Ensures proper browser cleanup and resource release.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully for thread: {}", Thread.currentThread().getName());
            } catch (Exception e) {
                logger.error("Error while quitting driver for thread: {}", 
                           Thread.currentThread().getName(), e);
            } finally {
                clearThreadLocal();
            }
        }
    }

    /**
     * Clears all thread-local storage for the current thread.
     * 
     * This should be called after driver.quit() to ensure proper cleanup
     * and prevent memory leaks in thread pool scenarios.
     */
    public static void clearThreadLocal() {
        driverThreadLocal.remove();
        optionsThreadLocal.remove();
        logger.debug("ThreadLocal storage cleared for thread: {}", Thread.currentThread().getName());
    }

    /**
     * Gets information about the current driver state.
     *
     * @return a string describing the current driver state
     */
    public static String getDriverInfo() {
        WebDriver driver = driverThreadLocal.get();
        DriverOptions options = optionsThreadLocal.get();
        
        if (driver == null) {
            return "No driver initialized for thread: " + Thread.currentThread().getName();
        }
        
        String browserInfo = options != null ? options.getBrowserType().getDisplayName() : "Unknown";
        return String.format("Driver info - Thread: %s, Browser: %s, URL: %s",
                           Thread.currentThread().getName(),
                           browserInfo,
                           driver.getCurrentUrl());
    }
}
