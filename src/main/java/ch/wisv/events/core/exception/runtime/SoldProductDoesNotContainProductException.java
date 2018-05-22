package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class SoldProductDoesNotContainProductException extends EventsRuntimeException {

    /**
     * SoldProductDoesNotContainProductException exception.
     */
    public SoldProductDoesNotContainProductException() {
        super(LogLevelEnum.WARN, "SoldProduct should contain a Product, before calling this method.");
    }
}
