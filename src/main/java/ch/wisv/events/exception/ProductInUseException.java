package ch.wisv.events.exception;

/**
 * Created by sven on 17/10/2016.
 */
public class ProductInUseException extends RuntimeException {

    public ProductInUseException(String message) {
        super(message);
    }
}
