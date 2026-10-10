package framework.config;

import framework.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton facade for accessing application configuration.
 * 
 * ConfigManager provides a clean, centralized interface for accessing
 * configuration values throughout the framework. It wraps ConfigurationLoader
 * and maintains a single Configuration instance per application session.
 * 
 * Configuration is loaded lazily on first access and cached for efficiency.
 * This ensures that configuration is not repeatedly loaded from disk.
 * 
 * Usage:
 * <pre>
 *   String browser = ConfigManager.getInstance().getBrowser();
 *   String baseUrl = ConfigManager.getInstance().getBaseUrl();
 *   int explicitWait = ConfigManager.getInstance().getExplicitWait();
 * </pre>
 * 
 * Environment Selection:
 * By default, the DEV environment is used. To specify a different environment:
 * <pre>
 *   java -Denv=test   // Uses config-test.properties
 *   java -Denv=uat    // Uses config-uat.properties
 *   java -Denv=prod   // Uses config-prod.properties
 * </pre>
 * 
 * System Property Overrides:
 * Any configuration value can be overridden via system properties:
 * <pre>
 *   java -Dbrowser=firefox                    // Override browser
 *   java -DbaseUrl=https://staging.example.com // Override base URL
 *   java -DexplicitWait=30                     // Override wait timeout
 * </pre>
 * 
 * Thread-Safe: Yes, through lazy initialization with proper synchronization
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Configuration configuration;

    /**
     * Private constructor - use getInstance() instead.
     */
    private ConfigManager() {
    }

    /**
     * Gets the singleton ConfigManager instance.
     * 
     * Configuration is loaded lazily on first access. Subsequent calls
     * return the cached configuration instance.
     *
     * @return the ConfigManager singleton instance
     * @throws ConfigurationException if configuration cannot be loaded
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
            instance.loadConfiguration();
        }
        return instance;
    }

    /**
     * Loads configuration from properties files.
     * 
     * Configuration is loaded using ConfigurationLoader which implements
     * a hierarchical loading strategy:
     * 1. Base configuration (config.properties)
     * 2. Environment-specific configuration (config-{env}.properties)
     * 3. System property overrides
     * 
     * This method is called automatically by getInstance().
     *
     * @throws ConfigurationException if configuration cannot be loaded
     */
    private void loadConfiguration() {
        try {
            String envProperty = System.getProperty("env", "dev");
            Environment environment = Environment.fromString(envProperty);
            this.configuration = ConfigurationLoader.load(environment);
            logger.info("Configuration loaded successfully for environment: {}", environment.getName());
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            throw new ConfigurationException("Failed to load configuration", e);
        }
    }

    /**
     * Resets the configuration and forces a reload on next access.
     * 
     * This is useful for testing scenarios where the environment changes
     * or system properties are modified.
     */
    public static synchronized void reset() {
        instance = null;
        logger.info("Configuration reset - will reload on next access");
    }

    /**
     * Gets the underlying Configuration object.
     *
     * @return the Configuration instance
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the current environment.
     *
     * @return the Environment (DEV, TEST, UAT, STAGING, PROD)
     */
    public Environment getEnvironment() {
        return configuration.getEnvironment();
    }

    /**
     * Gets the base URL for the application.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        return configuration.getBaseUrl();
    }

    /**
     * Gets the preferred browser type.
     *
     * @return the browser type (chrome, firefox, safari, edge)
     */
    public String getBrowser() {
        return configuration.getBrowser();
    }

    /**
     * Checks if headless browser mode is enabled.
     *
     * @return true if headless mode should be used
     */
    public boolean isHeadless() {
        return configuration.isHeadless();
    }

    /**
     * Gets the implicit wait timeout in seconds.
     *
     * @return the implicit wait timeout
     */
    public int getImplicitWait() {
        return configuration.getImplicitWait();
    }

    /**
     * Gets the explicit wait timeout in seconds.
     *
     * @return the explicit wait timeout
     */
    public int getExplicitWait() {
        return configuration.getExplicitWait();
    }

    /**
     * Gets the page load timeout in seconds.
     *
     * @return the page load timeout
     */
    public int getPageLoadTimeout() {
        return configuration.getPageLoadTimeout();
    }

    /**
     * Gets the script execution timeout in seconds.
     *
     * @return the script timeout
     */
    public int getScriptTimeout() {
        return configuration.getScriptTimeout();
    }

    /**
     * Checks if screenshots should be captured on failure.
     *
     * @return true if screenshots on failure are enabled
     */
    public boolean isScreenshotOnFailure() {
        return configuration.isScreenshotOnFailure();
    }

    /**
     * Gets the reports directory path.
     *
     * @return the reports directory
     */
    public String getReportsDirectory() {
        return configuration.getReportsDirectory();
    }

    /**
     * Gets a custom property value.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getProperty(String key) {
        return configuration.getProperty(key);
    }

    /**
     * Gets a custom property value with a default fallback.
     *
     * @param key the property key
     * @param defaultValue the default value if key not found
     * @return the property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return configuration.getProperty(key, defaultValue);
    }

    /**
     * Checks if a specific property exists.
     *
     * @param key the property key
     * @return true if the property exists
     */
    public boolean hasProperty(String key) {
        return configuration.hasProperty(key);
    }

    @Override
    public String toString() {
        return "ConfigManager{" +
                "environment=" + (configuration != null ? configuration.getEnvironment().getName() : "NOT_LOADED") +
                ", configuration=" + configuration +
                '}';
    }
}
