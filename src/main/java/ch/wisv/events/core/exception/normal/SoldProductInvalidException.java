package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class SoldProductInvalidException extends EventsException {

    /**
     * CustomerNotFoundException.
     *
     * @param message of type String
     */
    public SoldProductInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
