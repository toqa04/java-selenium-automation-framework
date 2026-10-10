package framework.config;

import framework.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Legacy configuration loader - delegates to ConfigurationLoader for backward compatibility.
 * 
 * This class is maintained for backward compatibility with existing code.
 * New code should use ConfigurationLoader directly.
 * 
 * @deprecated Use ConfigurationLoader instead
 * @author Automation Team
 * @version 1.0.0
 */
@Deprecated
public class ConfigLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    /**
     * Loads configuration for the specified environment.
     * Delegates to ConfigurationLoader.
     *
     * @param environment the target environment
     * @return loaded Configuration object
     * @throws ConfigurationException if configuration cannot be loaded
     * @deprecated Use ConfigurationLoader.load(Environment) instead
     */
    @Deprecated
    public static Configuration load(Environment environment) {
        logger.warn("ConfigLoader.load() is deprecated. Use ConfigurationLoader.load() instead.");
        return ConfigurationLoader.load(environment);
    }

    /**
     * Loads configuration based on environment parameter from system properties.
     * Delegates to ConfigurationLoader.
     *
     * @return loaded Configuration object
     * @throws ConfigurationException if configuration cannot be loaded
     * @deprecated Use ConfigurationLoader.load() instead
     */
    @Deprecated
    public static Configuration load() {
        logger.warn("ConfigLoader.load() is deprecated. Use ConfigurationLoader.load() instead.");
        return ConfigurationLoader.load();
    }
}

