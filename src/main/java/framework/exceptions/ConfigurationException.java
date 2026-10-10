package framework.exceptions;

/**
 * Exception thrown when configuration errors occur.
 * 
 * This includes missing configuration files, invalid properties, and environment setup failures.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class ConfigurationException extends FrameworkException {

    /**
     * Constructs a ConfigurationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a ConfigurationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a ConfigurationException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
