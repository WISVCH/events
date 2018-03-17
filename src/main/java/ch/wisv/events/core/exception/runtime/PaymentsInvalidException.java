package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 *
 */
public class PaymentsInvalidException extends EventsRuntimeException {

    /**
     * @param message of type String
     */
    public PaymentsInvalidException(String message) {
        super(LogLevelEnum.ERROR, message);
    }
}
