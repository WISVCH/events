package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * TicketNotFoundException class.
 */
public class TicketNotFoundException extends EventsException {

    /**
     * TicketNotFoundException.
     */
    public TicketNotFoundException() {
        super(LogLevelEnum.DEBUG, "Ticket has not been found!");
    }

    /**
     * TicketNotFoundException.
     *
     * @param message of type String
     */
    public TicketNotFoundException(String message) {
        super(LogLevelEnum.DEBUG, message);
    }
}
