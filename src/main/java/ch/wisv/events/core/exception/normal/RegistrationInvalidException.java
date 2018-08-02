package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * RegistrationInvalidException class.
 */
public class RegistrationInvalidException extends EventsException {

    /**
     * CustomerInvalidException.
     *
     * @param message of type String
     */
    public RegistrationInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
