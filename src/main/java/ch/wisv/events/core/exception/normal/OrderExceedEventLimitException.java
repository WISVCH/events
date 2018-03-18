package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class OrderExceedEventLimitException extends EventsException {

    public OrderExceedEventLimitException(Integer leftOver) {
        super(LogLevelEnum.WARN, "Limit reached after " + leftOver.toString() + " webshop.");
    }
}
