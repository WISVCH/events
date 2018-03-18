package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class EventNotFoundException extends EventsException {

    /**
     * EventNotFoundException.
     *
     * @param message of type String
     */
    public EventNotFoundException(String message) {
        super(LogLevelEnum.WARN, "Event with " + message + " not found!");
    }
}
