package framework.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration container for all framework and test settings.
 *
 * This class uses immutable maps to prevent accidental modifications and
 * provides typed access methods for common configuration properties.
 *
 * Thread-safe through defensive copying and immutable collections.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class Configuration {

    private final Environment environment;
    private final Map<String, String> properties;

    /**
     * Constructs a Configuration with the specified environment and properties.
     *
     * @param environment the target environment
     * @param properties the configuration properties (will be copied)
     */
    public Configuration(Environment environment, Map<String, String> properties) {
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.properties = new HashMap<>(Objects.requireNonNull(properties, "Properties cannot be null"));
    }

    /**
     * Gets the target environment.
     *
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Gets all configuration properties. Returns a copy to prevent external
     * modifications.
     *
     * @return a copy of the properties map
     */
    public Map<String, String> getProperties() {
        return new HashMap<>(properties);
    }

    /**
     * Gets a property value by key.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Gets a property value with a default fallback.
     *
     * @param key the property key
     * @param defaultValue the default value if key not found
     * @return the property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    /**
     * Gets the target application URL.
     *
     * @return the base URL for the application
     */
    public String getApplicationUrl() {
        return getProperty("baseUrl", getProperty("application.url", ""));
    }

    /**
     * Gets the preferred browser type.
     *
     * @return the browser type (chrome, firefox, safari, edge)
     */
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }

    /**
     * Gets the explicit wait timeout in seconds.
     *
     * @return the wait timeout
     */
    public int getExplicitWaitSeconds() {
        String value = getProperty("explicitWait", getProperty("wait.explicit.seconds", "10"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 10; // Default fallback
        }
    }

    /**
     * Gets the implicit wait timeout in seconds.
     *
     * @return the implicit wait timeout
     */
    public int getImplicitWaitSeconds() {
        String value = getProperty("implicitWait", getProperty("wait.implicit.seconds", "0"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // Default: no implicit wait (prefer explicit)
        }
    }

    /**
     * Gets the page load timeout in seconds.
     *
     * @return the page load timeout
     */
    public int getPageLoadTimeoutSeconds() {
        String value = getProperty("pageLoadTimeout", getProperty("wait.pageload.seconds", "30"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 30; // Default fallback
        }
    }

    /**
     * Checks if headless mode is enabled.
     *
     * @return true if headless mode should be used
     */
    public boolean isHeadlessMode() {
        return Boolean.parseBoolean(getProperty("headless", getProperty("browser.headless", "false")));
    }

    /**
     * Checks if screenshots should be captured on failure.
     *
     * @return true if screenshots on failure are enabled
     */
    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshotOnFailure", getProperty("screenshot.on.failure", "true")));
    }

    /**
     * Checks if a screenshot should be captured after every test, including
     * successful and skipped tests.
     *
     * @return true when every test should produce a screenshot
     */
    public boolean isScreenshotAlways() {
        return Boolean.parseBoolean(getProperty("screenshotAlways", getProperty("screenshot.always", "false")));
    }

    /**
     * Gets the delay, in seconds, before taking a screenshot.
     *
     * @return screenshot delay in seconds
     */
    public int getScreenshotDelaySeconds() {
        String value = getProperty("screenshotDelaySeconds", getProperty("screenshot.delay.seconds", "3"));
        try {
            return Math.max(0, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    /**
     * Gets the screenshot directory path.
     *
     * @return the screenshot directory
     */
    public String getScreenshotDirectory() {
        return getProperty("screenshotsDirectory", getProperty("screenshot.directory", "screenshots"));
    }

    /**
     * Gets the report directory path.
     *
     * @return the report directory
     */
    public String getReportDirectory() {
        return getProperty("reportsDirectory", getProperty("report.directory", "reports"));
    }

    /**
     * Checks if the browser should start in maximized mode.
     *
     * @return true if browser should be maximized
     */
    public boolean isMaximizeBrowser() {
        return Boolean.parseBoolean(getProperty("maximize", getProperty("browser.maximize", "true")));
    }

    /**
     * Gets the logging level.
     *
     * @return the log level (DEBUG, INFO, WARN, ERROR)
     */
    public String getLogLevel() {
        return getProperty("log.level", "INFO");
    }

    /**
     * Checks if a specific property exists.
     *
     * @param key the property key
     * @return true if the property exists
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    // ================== New TASK 03 Configuration Methods ==================
    /**
     * Gets the base URL for the application. This is the primary URL for test
     * execution.
     *
     * @return the base URL, or empty string if not configured
     */
    public String getBaseUrl() {
        return getApplicationUrl();
    }

    /**
     * Checks if headless browser mode is enabled. Alternative to
     * isHeadlessMode() using new property names.
     *
     * @return true if headless mode should be used
     */
    public boolean isHeadless() {
        return isHeadlessMode();
    }

    /**
     * Gets the implicit wait timeout in seconds. Converts from seconds using
     * new property name format.
     *
     * @return the implicit wait timeout in seconds
     */
    public int getImplicitWait() {
        String value = getProperty("implicitWait", getProperty("wait.implicit.seconds", "0"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid implicitWait value: " + value + " (must be a valid integer)");
        }
    }

    /**
     * Gets the explicit/WebDriverWait timeout in seconds. Used for explicit
     * wait operations (WebDriverWait).
     *
     * @return the explicit wait timeout in seconds
     */
    public int getExplicitWait() {
        String value = getProperty("explicitWait", getProperty("wait.explicit.seconds", "20"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid explicitWait value: " + value + " (must be a valid integer)");
        }
    }

    /**
     * Gets the page load timeout in seconds. Time to wait for page loading to
     * complete.
     *
     * @return the page load timeout in seconds
     */
    public int getPageLoadTimeout() {
        String value = getProperty("pageLoadTimeout", getProperty("wait.pageload.seconds", "30"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid pageLoadTimeout value: " + value + " (must be a valid integer)");
        }
    }

    /**
     * Gets the script execution timeout in seconds. Time to wait for JavaScript
     * execution to complete.
     *
     * @return the script timeout in seconds
     */
    public int getScriptTimeout() {
        String value = getProperty("scriptTimeout", "30");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid scriptTimeout value: " + value + " (must be a valid integer)");
        }
    }

    /**
     * Gets the reports directory path. Alternative to getReportDirectory()
     * using new property name format.
     *
     * @return the reports directory
     */
    public String getReportsDirectory() {
        return getProperty("reportsDirectory", "reports");
    }

    // ========================================================================
    @Override
    public String toString() {
        return "Configuration{"
                + "environment=" + environment.getName()
                + ", properties=" + properties.size() + " entries"
                + '}';
    }
}
