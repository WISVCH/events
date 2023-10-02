package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * TicketPassFailedException class.
 */
public class TicketPassFailedException extends EventsException {

    /**
     * TicketPassFailedException.
     */
    public TicketPassFailedException() {
        super(LogLevelEnum.DEBUG, "Ticket has not been found!");
    }

    /**
     * TicketPassFailedException.
     *
     * @param message of type String
     */
    public TicketPassFailedException(String message) {
        super(LogLevelEnum.DEBUG, message);
    }
}
