package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class PaymentsConnectionException extends EventsRuntimeException {

    /**
     * ProductAlreadyLinkedException exception.
     */
    public PaymentsConnectionException() {
        super(LogLevelEnum.ERROR, "Can not connect to CH Payments");
    }
}
