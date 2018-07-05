package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * TicketAlreadyScannedException class.
 */
public class TicketAlreadyScannedException extends EventsException {

    /**
     * TicketAlreadyScannedException.
     *
     * @param message of type String
     */
    public TicketAlreadyScannedException(String message) {
        super(LogLevelEnum.DEBUG, message);
    }
}
