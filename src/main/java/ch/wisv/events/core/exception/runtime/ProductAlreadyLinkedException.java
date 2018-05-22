package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class ProductAlreadyLinkedException extends EventsRuntimeException {

    /**
     * ProductAlreadyLinkedException exception.
     */
    public ProductAlreadyLinkedException() {
        super(LogLevelEnum.ERROR, "Product is already added to an Event or Product");
    }
}
