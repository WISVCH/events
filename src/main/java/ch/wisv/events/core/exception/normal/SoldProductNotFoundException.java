package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class SoldProductNotFoundException extends EventsException {

    /**
     * CustomerNotFoundException.
     *
     * @param message of type String
     */
    public SoldProductNotFoundException(String message) {
        super(LogLevelEnum.WARN, "SoldProduct with " + message + " not found!");
    }
}
