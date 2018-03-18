package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class UserNotInBetaException extends EventsRuntimeException {

    public UserNotInBetaException() {
        super(LogLevelEnum.INFO, "User is not a beta program!");
    }
}
