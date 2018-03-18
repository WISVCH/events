package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class PaymentsStatusUnknown extends EventsException {

    /**
     * PaymentsStatusUnknown.
     *
     * @param status of type String
     */
    public PaymentsStatusUnknown(String status) {
        super(LogLevelEnum.ERROR, "Payments status " + status + " is unknown!");
    }
}
