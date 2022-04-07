package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * TicketNotFoundException class.
 */
public class TicketNotTransferableException extends EventsException {

    /**
     * TicketNotFoundException.
     */
    public TicketNotTransferableException() {
        super(LogLevelEnum.DEBUG, "Ticket is not transferable");
    }

    /**
     * TicketNotFoundException.
     *
     * @param message of type String
     */
    public TicketNotTransferableException(String message) {
        super(LogLevelEnum.DEBUG, message);
    }
}
