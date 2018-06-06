package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * OrderInvalidException class.
 */
public class OrderInvalidException extends EventsException {

    /**
     * OrderInvalidException.
     *
     * @param message of type String
     */
    public OrderInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
