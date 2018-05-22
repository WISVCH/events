package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class UnassignedOrderException extends EventsException {

    public UnassignedOrderException() {
        super(LogLevelEnum.ERROR, "Order is not assigned to a Customer");
    }
}
