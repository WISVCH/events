package ch.wisv.events.domain.exception;

/**
 * OrderInvalidException class.
 */
public class OrderInvalidException extends RuntimeException {

    /**
     * OrderInvalidException.
     *
     * @param message of type String
     */
    public OrderInvalidException(String message) {
        super(message);
    }
}
