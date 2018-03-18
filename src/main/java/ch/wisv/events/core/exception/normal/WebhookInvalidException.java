package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class WebhookInvalidException extends EventsException {

    /**
     * WebhookInvalidException.
     *
     * @param message of type String
     */
    public WebhookInvalidException(String message) {
        super(LogLevelEnum.WARN, message);
    }
}
