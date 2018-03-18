package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class EventsRuntimeException extends RuntimeException {

    public EventsRuntimeException(LogLevelEnum logEnum, String message) {
        super(message);
        logEnum.logMessage(message);
    }
}
