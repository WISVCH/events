package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * PaymentsConnectionException class.
 */
public class PaymentsConnectionException extends EventsRuntimeException {

    /**
     * ProductAlreadyLinkedException exception.
     *
     * @param ex of type String
     */
    public PaymentsConnectionException(String ex) {
        super(LogLevelEnum.ERROR, ex);
    }
}
