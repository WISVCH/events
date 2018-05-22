package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class EventsException extends Exception {

    public EventsException(LogLevelEnum logEnum, String message) {
        super(message);
        logEnum.logMessage(message);
    }
}
