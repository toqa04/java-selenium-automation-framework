package framework.exceptions;

/**
 * Exception thrown when an explicit wait condition fails.
 *
 * This exception is used to distinguish wait-related failures from general
 * page or driver issues. It includes useful context such as locator,
 * timeout, and the expected condition so the failure is actionable.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class WaitException extends FrameworkException {

    /**
     * Constructs a WaitException with the specified detail message.
     *
     * @param message the detail message
     */
    public WaitException(String message) {
        super(message);
    }

    /**
     * Constructs a WaitException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public WaitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a WaitException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public WaitException(Throwable cause) {
        super(cause);
    }
}
