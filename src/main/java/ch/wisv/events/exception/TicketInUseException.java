package ch.wisv.events.exception;

/**
 * Created by sven on 17/10/2016.
 */
public class TicketInUseException extends RuntimeException {

    public TicketInUseException(String message) {
        super(message);
    }
}
