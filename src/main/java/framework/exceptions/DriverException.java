package framework.exceptions;

/**
 * Exception thrown when WebDriver operations fail.
 * 
 * This includes driver initialization, browser setup, and browser operations.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class DriverException extends FrameworkException {

    /**
     * Constructs a DriverException with the specified detail message.
     *
     * @param message the detail message
     */
    public DriverException(String message) {
        super(message);
    }

    /**
     * Constructs a DriverException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a DriverException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public DriverException(Throwable cause) {
        super(cause);
    }
}
