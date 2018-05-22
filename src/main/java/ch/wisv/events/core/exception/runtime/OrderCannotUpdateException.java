package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class OrderCannotUpdateException extends EventsRuntimeException {

    /**
     * OrderCannotUpdateException exception.
     */
    public OrderCannotUpdateException() {
        super(LogLevelEnum.ERROR, "This object is new so can not be updated");
    }
}
