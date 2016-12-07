package ch.wisv.events.core.exception;

/**
 * ProductInUseException.
 */
public class ProductInUseException extends RuntimeException {

    /**
     * Default constructor
     *
     * @param message message in the exception.
     */
    public ProductInUseException(String message) {
        super(message);
    }

}
