package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class OrderExceedProductLimitException extends EventsException {

    public OrderExceedProductLimitException(Integer leftOver) {
        super(LogLevelEnum.WARN, "Product limit exceeded (max " + leftOver.toString() + " tickets allowed).");
    }
}
