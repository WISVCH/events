package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * PaymentsInvalidException class.
 */
public class PaymentsInvalidException extends EventsRuntimeException {

    /**
     * PaymentsInvalidException constructor.
     *
     * @param message of type String
     */
    public PaymentsInvalidException(String message) {
        super(LogLevelEnum.ERROR, message);
    }
}
