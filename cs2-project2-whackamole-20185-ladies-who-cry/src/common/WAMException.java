package common;

public class WAMException extends Exception {
    /**
     * @param message The error message associated with the exception.
     */
    public WAMException(String message) {
        super(message);
    }

    /**
     * @param cause The root cause of the exception.
     */
    public WAMException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message The message associated with the exception.
     * @param cause The root cause of the exception.
     */
    public WAMException(String message, Throwable cause) {
        super(message, cause);
    }
}
