package framework.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for discovering and managing available test screens.
 * 
 * Implements the Registry design pattern.
 * Allows dynamic registration and discovery of page objects without modification to framework.
 * 
 * Screen metadata includes:
 * - Screen name/identifier
 * - Screen class reference
 * - URL pattern (optional)
 * - Description
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class ScreenRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenRegistry.class);
    private static final Map<String, ScreenMetadata> screenRegistry = new HashMap<>();

    /**
     * Registers a screen in the registry.
     *
     * @param screenId the unique screen identifier
     * @param metadata the screen metadata
     */
    public static void registerScreen(String screenId, ScreenMetadata metadata) {
        if (screenRegistry.containsKey(screenId)) {
            logger.warn("Screen already registered: {}", screenId);
            return;
        }
        
        screenRegistry.put(screenId, metadata);
        logger.info("Screen registered: {} ({})", screenId, metadata.getScreenClassName());
    }

    /**
     * Gets screen metadata by ID.
     *
     * @param screenId the screen identifier
     * @return the screen metadata or null if not found
     */
    public static ScreenMetadata getScreen(String screenId) {
        ScreenMetadata metadata = screenRegistry.get(screenId);
        if (metadata == null) {
            logger.warn("Screen not found in registry: {}", screenId);
        }
        return metadata;
    }

    /**
     * Gets all registered screens.
     *
     * @return list of all screen metadata
     */
    public static List<ScreenMetadata> getAllScreens() {
        return new ArrayList<>(screenRegistry.values());
    }

    /**
     * Gets all registered screen IDs.
     *
     * @return list of screen IDs
     */
    public static List<String> getAllScreenIds() {
        return new ArrayList<>(screenRegistry.keySet());
    }

    /**
     * Checks if a screen is registered.
     *
     * @param screenId the screen identifier
     * @return true if screen is registered
     */
    public static boolean isScreenRegistered(String screenId) {
        return screenRegistry.containsKey(screenId);
    }

    /**
     * Unregisters a screen (for cleanup or testing).
     *
     * @param screenId the screen identifier
     */
    public static void unregisterScreen(String screenId) {
        if (screenRegistry.remove(screenId) != null) {
            logger.info("Screen unregistered: {}", screenId);
        }
    }

    /**
     * Clears all registered screens.
     */
    public static void clear() {
        screenRegistry.clear();
        logger.info("Screen registry cleared");
    }

    /**
     * Gets the total number of registered screens.
     *
     * @return the number of screens
     */
    public static int getScreenCount() {
        return screenRegistry.size();
    }

    /**
     * Data class for screen metadata.
     */
    public static class ScreenMetadata {
        private final String screenName;
        private final String screenClassName;
        private final String urlPattern;
        private final String description;

        public ScreenMetadata(String screenName, String screenClassName, 
                            String urlPattern, String description) {
            this.screenName = screenName;
            this.screenClassName = screenClassName;
            this.urlPattern = urlPattern;
            this.description = description;
        }

        public String getScreenName() {
            return screenName;
        }

        public String getScreenClassName() {
            return screenClassName;
        }

        public String getUrlPattern() {
            return urlPattern;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "ScreenMetadata{" +
                    "screenName='" + screenName + '\'' +
                    ", screenClassName='" + screenClassName + '\'' +
                    ", urlPattern='" + urlPattern + '\'' +
                    '}';
        }
    }
}
