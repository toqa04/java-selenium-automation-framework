package framework.config;

/**
 * Enum representing different test environments.
 * 
 * This enum is used to select and configure the appropriate environment
 * for test execution (development, staging, production).
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public enum Environment {
    DEV("dev", "Development environment"),
    TEST("test", "Test environment"),
    UAT("uat", "User Acceptance Testing environment"),
    STAGING("staging", "Staging environment"),
    PROD("prod", "Production environment");

    private final String name;
    private final String description;

    /**
     * Constructs an Environment with the specified name and description.
     *
     * @param name the environment name (used in property file names)
     * @param description human-readable description
     */
    Environment(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the environment name.
     *
     * @return the environment name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the environment description.
     *
     * @return the environment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Parses an environment from a string value.
     *
     * @param value the environment value (case-insensitive)
     * @return the corresponding Environment enum value
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static Environment fromString(String value) {
        if (value == null || value.isEmpty()) {
            return DEV; // Default to development
        }
        for (Environment env : Environment.values()) {
            if (env.name.equalsIgnoreCase(value)) {
                return env;
            }
        }
        throw new IllegalArgumentException("Unknown environment: " + value);
    }
}
