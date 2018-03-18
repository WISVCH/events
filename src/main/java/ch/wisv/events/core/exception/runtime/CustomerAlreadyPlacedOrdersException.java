package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class CustomerAlreadyPlacedOrdersException extends EventsRuntimeException {

    /**
     * CustomerAlreadyPlacedOrdersException exception.
     */
    public CustomerAlreadyPlacedOrdersException() {
        super(LogLevelEnum.WARN, "Customer has already placed orders, so it can not be deleted!");
    }
}
