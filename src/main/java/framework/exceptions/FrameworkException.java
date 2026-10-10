package framework.exceptions;

/**
 * Base exception class for the automation framework.
 * All framework-related exceptions should inherit from this class.
 * 
 * This exception is thrown for generic framework errors that don't fit
 * into more specific exception categories.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class FrameworkException extends RuntimeException {

    /**
     * Constructs a FrameworkException with the specified detail message.
     *
     * @param message the detail message
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * Constructs a FrameworkException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a FrameworkException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public FrameworkException(Throwable cause) {
        super(cause);
    }
}
