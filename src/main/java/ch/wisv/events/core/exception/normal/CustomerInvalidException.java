package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class CustomerInvalidException extends EventsException {

    /**
     * CustomerInvalidException.
     *
     * @param message of type String
     */
    public CustomerInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
