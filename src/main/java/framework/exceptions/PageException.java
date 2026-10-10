package framework.exceptions;

/**
 * Exception thrown when page operations fail.
 * 
 * This includes element not found, page navigation failures, and action timeouts.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class PageException extends FrameworkException {

    /**
     * Constructs a PageException with the specified detail message.
     *
     * @param message the detail message
     */
    public PageException(String message) {
        super(message);
    }

    /**
     * Constructs a PageException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public PageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a PageException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public PageException(Throwable cause) {
        super(cause);
    }
}
