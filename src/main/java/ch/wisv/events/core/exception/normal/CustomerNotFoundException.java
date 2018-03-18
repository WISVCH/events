package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class CustomerNotFoundException extends EventsException {

    /**
     * CustomerNotFoundException.
     *
     * @param message of type String
     */
    public CustomerNotFoundException(String message) {
        super(LogLevelEnum.WARN, "Customer with " + message + " not found!");
    }
}
