package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class OrderExceedEventLimitException extends EventsException {

    public OrderExceedEventLimitException(Integer leftOver) {
        super(LogLevelEnum.WARN, "Event limit exceeded (max " + leftOver.toString() + " tickets allowed).");
    }
}
