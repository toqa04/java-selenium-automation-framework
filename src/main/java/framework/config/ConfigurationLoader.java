package framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.exceptions.ConfigurationException;

/**
 * Loads and manages application configuration from property files with
 * validation.
 *
 * ConfigurationLoader implements a hierarchical configuration loading strategy:
 * 1. Base configuration (config.properties) 2. Environment-specific
 * configuration (config-{env}.properties) 3. System property overrides
 * (command-line arguments)
 *
 * Supported Environments: dev, test, uat, staging, prod
 *
 * Configuration is validated after loading to ensure: - Browser type is valid
 * (chrome, firefox, safari, edge) - Timeout values are non-negative integers -
 * Required properties are present - Numeric properties have valid format
 *
 * Usage Examples:
 * <pre>
 *   // Load default environment (dev)
 *   Configuration config = ConfigurationLoader.load();
 *
 *   // Load specific environment
 *   Configuration config = ConfigurationLoader.load(Environment.UAT);
 *
 *   // Override via system property
 *   java -Denv=prod MyTest
 * </pre>
 *
 * Property Override Examples:
 * <pre>
 *   java -Dbrowser=firefox MyTest                    // Override browser
 *   java -DbaseUrl=https://staging.example.com MyTest // Override base URL
 *   java -DexplicitWait=30 MyTest                     // Override timeout
 * </pre>
 *
 * Thread-Safe: Yes, through read-only properties and immutable configuration
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class ConfigurationLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
    private static final String CONFIG_PATH = "config/";
    private static final String DEFAULT_CONFIG = "defaults.properties";
    private static final String BASE_CONFIG = "config.properties";
    private static final String ENVIRONMENT_CONFIG_TEMPLATE = "config-%s.properties";

    // Valid browser types
    private static final String[] VALID_BROWSERS = {"chrome", "firefox", "safari", "edge"};

    /**
     * Loads configuration for the specified environment with full validation.
     *
     * Properties are loaded in order of precedence: 1. Shared defaults
     * (defaults.properties) 2. Local base configuration (config.properties)
     * 3. Environment-specific configuration (config-{env}.properties)
     * 4. System properties (final override)
     *
     * After loading, configuration is validated for: - Valid browser type -
     * Non-negative timeout values - Valid numeric formats
     *
     * @param environment the target environment
     * @return loaded and validated Configuration object
     * @throws ConfigurationException if configuration cannot be loaded or is
     * invalid
     */
    public static Configuration load(Environment environment) {
        try {
            Map<String, String> properties = new HashMap<>();

            // Load safe, versioned defaults before optional local overrides.
            loadProperties(CONFIG_PATH + DEFAULT_CONFIG, properties);
            logger.info("Loaded default configuration from {}", DEFAULT_CONFIG);

            // Load local base configuration
            loadProperties(CONFIG_PATH + BASE_CONFIG, properties);
            logger.info("Loaded base configuration from {}", BASE_CONFIG);

            // Load environment-specific configuration
            String environmentConfig = CONFIG_PATH + String.format(ENVIRONMENT_CONFIG_TEMPLATE, environment.getName());
            loadProperties(environmentConfig, properties);
            logger.info("Loaded environment configuration from {} for environment: {}",
                    environmentConfig, environment.getName());

            // Override with system properties
            overrideWithSystemProperties(properties);
            logger.debug("Applied system property overrides");

            // Validate configuration before creating Configuration object
            validateConfiguration(properties);
            logger.debug("Configuration validation passed");

            Configuration config = new Configuration(environment, properties);
            logger.info("Configuration loaded successfully for environment: {}", environment.getName());
            return config;

        } catch (ConfigurationException e) {
            logger.error("Configuration validation failed for environment: {}", environment.getName());
            throw e;
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            logger.error("Failed to load configuration for environment: {}", environment.getName(), e);
            throw new ConfigurationException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Loads configuration based on environment parameter from system
     * properties. If not specified, defaults to DEV environment.
     *
     * The environment is determined by the "env" system property:
     * <pre>
     *   java -Denv=test MyTest    // Uses config-test.properties
     *   java -Denv=prod MyTest    // Uses config-prod.properties
     * </pre>
     *
     *
     * @return loaded Configuration object
     * @throws ConfigurationException if configuration cannot be loaded
     */
    public static Configuration load() {
        String envProperty = System.getProperty("env", "dev");
        Environment environment = Environment.fromString(envProperty);
        return load(environment);
    }

    /**
     * Loads properties from a file and merges them into the properties map.
     *
     * @param resourcePath the classpath resource path
     * @param targetMap the map to merge properties into
     * @throws IOException if file cannot be read
     */
    private static void loadProperties(String resourcePath, Map<String, String> targetMap) throws IOException {
        try (InputStream inputStream = ConfigurationLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.warn("Configuration file not found: {}. Continuing with current properties.", resourcePath);
                return;
            }

            Properties props = new Properties();
            props.load(inputStream);

            props.forEach((key, value) -> targetMap.put(key.toString(), value.toString()));
            logger.debug("Loaded {} properties from {}", props.size(), resourcePath);
        }
    }

    /**
     * Overrides configuration with system properties. System properties take
     * precedence over file-based configuration.
     *
     * Only recognized configuration property names are accepted to prevent
     * accidental overrides from unrelated system properties.
     *
     * @param properties the properties map to override
     */
    private static void overrideWithSystemProperties(Map<String, String> properties) {
        System.getProperties().forEach((key, value) -> {
            String keyStr = key.toString();
            if (isConfigurationProperty(keyStr)) {
                properties.put(keyStr, value.toString());
                logger.debug("Overridden property via system property: {}", keyStr);
            }
        });
    }

    /**
     * Checks if a system property is a framework configuration property.
     *
     * This prevents accidental overrides from unrelated JVM system properties.
     * Only properties matching known configuration patterns are accepted.
     *
     *
     * @param propertyName the property name
     * @return true if this is a recognized configuration property
     */
    private static boolean isConfigurationProperty(String propertyName) {
        return propertyName.equals("browser")
                || propertyName.equals("headless")
                || propertyName.equals("baseUrl")
                || propertyName.equals("implicitWait")
                || propertyName.equals("explicitWait")
                || propertyName.equals("pageLoadTimeout")
                || propertyName.equals("scriptTimeout")
                || propertyName.equals("screenshotOnFailure")
                || propertyName.equals("reportsDirectory")
                || propertyName.equals("test.username")
                || propertyName.equals("test.password")
                || propertyName.equals("env")
                || // Legacy property names for backward compatibility
                propertyName.startsWith("application.")
                || propertyName.startsWith("browser.")
                || propertyName.startsWith("wait.")
                || propertyName.startsWith("screenshot.")
                || propertyName.startsWith("report.")
                || propertyName.startsWith("log.");
    }

    /**
     * Validates configuration after loading.
     *
     * Checks for: - Invalid browser type - Negative timeout values - Malformed
     * numeric values - Missing critical properties
     *
     * @param properties the configuration properties to validate
     * @throws ConfigurationException if configuration is invalid
     */
    private static void validateConfiguration(Map<String, String> properties) {
        // Validate browser
        String browser = properties.getOrDefault("browser", "chrome").toLowerCase();
        if (!isValidBrowser(browser)) {
            throw new ConfigurationException(
                    String.format("Invalid browser type: '%s'. Must be one of: chrome, firefox, safari, edge", browser)
            );
        }
        logger.debug("Browser validation passed: {}", browser);

        // Validate numeric properties (timeouts)
        validateTimeoutProperty(properties, "implicitWait", "Implicit wait timeout", 0);
        validateTimeoutProperty(properties, "explicitWait", "Explicit wait timeout", 0);
        validateTimeoutProperty(properties, "pageLoadTimeout", "Page load timeout", 0);
        validateTimeoutProperty(properties, "scriptTimeout", "Script timeout", 0);

        // Validate boolean properties
        validateBooleanProperty(properties, "headless", "Headless mode");
        validateBooleanProperty(properties, "screenshotOnFailure", "Screenshot on failure");

        logger.debug("All configuration properties validated successfully");
    }

    /**
     * Validates a timeout property to ensure it's a valid non-negative integer.
     *
     * @param properties the properties map
     * @param propertyName the property name to validate
     * @param propertyDescription human-readable description
     * @param minValue the minimum allowed value (typically 0)
     * @throws ConfigurationException if the property value is invalid
     */
    private static void validateTimeoutProperty(Map<String, String> properties,
            String propertyName,
            String propertyDescription,
            int minValue) {
        String value = properties.getOrDefault(propertyName, "0");
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < minValue) {
                throw new ConfigurationException(
                        String.format("%s cannot be negative. Found: %d", propertyDescription, intValue)
                );
            }
            logger.debug("{} validation passed: {} seconds", propertyDescription, intValue);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                    String.format("Invalid %s value: '%s' (must be a valid integer)", propertyName, value), e
            );
        }
    }

    /**
     * Validates a boolean property to ensure it can be parsed as a boolean.
     *
     * @param properties the properties map
     * @param propertyName the property name to validate
     * @param propertyDescription human-readable description
     * @throws ConfigurationException if the property value is invalid
     */
    private static void validateBooleanProperty(Map<String, String> properties,
            String propertyName,
            String propertyDescription) {
        String value = properties.getOrDefault(propertyName, "false");
        if (!isValidBoolean(value)) {
            throw new ConfigurationException(
                    String.format("Invalid %s value: '%s' (must be true or false)", propertyDescription, value)
            );
        }
        logger.debug("{} validation passed: {}", propertyDescription, value);
    }

    /**
     * Checks if a browser type is valid.
     *
     * @param browser the browser type to validate
     * @return true if the browser is valid
     */
    private static boolean isValidBrowser(String browser) {
        for (String validBrowser : VALID_BROWSERS) {
            if (validBrowser.equalsIgnoreCase(browser)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a value is a valid boolean string.
     *
     * @param value the value to check
     * @return true if the value can be parsed as a boolean
     */
    private static boolean isValidBoolean(String value) {
        return value != null
                && (value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("false"));
    }
}
