package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class ProductNotFoundException extends EventsException {

    /**
     * ProductNotFoundException.
     *
     * @param message of type String
     */
    public ProductNotFoundException(String message) {
        super(LogLevelEnum.WARN, "Product with " + message + " not found!");
    }
}
