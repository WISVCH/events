package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;
import ch.wisv.events.core.exception.runtime.EventsRuntimeException;

/**
 * OrderInvalidException class.
 */
public class OrderInvalidException extends EventsRuntimeException {

    /**
     * OrderInvalidException.
     *
     * @param message of type String
     */
    public OrderInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
