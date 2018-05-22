package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class EventInvalidException extends EventsException {

    /**
     * EventInvalidException.
     *
     * @param message of type String
     */
    public EventInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
