package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class OrderExceedCustomerLimitException extends EventsException {

    public OrderExceedCustomerLimitException(Integer leftOver) {
        super(LogLevelEnum.WARN, "Customer limit exceeded (max " + leftOver.toString() + " tickets allowed).");
    }
}
