package framework.driver;

/**
 * Enum representing supported browsers for automation.
 * 
 * This enum defines all browsers that the framework can automate.
 * Additional browsers can be added by extending this enum and implementing
 * support in WebDriverFactory.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public enum BrowserType {
    CHROME("chrome", "Google Chrome"),
    FIREFOX("firefox", "Mozilla Firefox"),
    SAFARI("safari", "Apple Safari"),
    EDGE("edge", "Microsoft Edge");

    private final String name;
    private final String displayName;

    /**
     * Constructs a BrowserType with the specified name and display name.
     *
     * @param name the browser name (used in configuration)
     * @param displayName human-readable name
     */
    BrowserType(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    /**
     * Gets the browser name.
     *
     * @return the browser name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the browser display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a browser type from a string value.
     *
     * @param value the browser value (case-insensitive)
     * @return the corresponding BrowserType enum value
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static BrowserType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return CHROME; // Default to Chrome
        }
        for (BrowserType browser : BrowserType.values()) {
            if (browser.name.equalsIgnoreCase(value)) {
                return browser;
            }
        }
        throw new IllegalArgumentException("Unsupported browser: " + value);
    }
}
