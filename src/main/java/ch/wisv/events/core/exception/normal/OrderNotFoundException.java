package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class OrderNotFoundException extends EventsException {

    /**
     * OrderNotFoundException.
     *
     * @param message of type String
     */
    public OrderNotFoundException(String message) {
        super(LogLevelEnum.WARN, "Order with " + message + " not found!");
    }
}
