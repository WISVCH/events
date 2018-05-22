package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class ProductInvalidException extends EventsException {

    /**
     * ProductInvalidException.
     *
     * @param message of type String
     */
    public ProductInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
