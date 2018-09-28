package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class WebhookNotFoundException extends EventsException {

    /**
     * WebhookNotFoundException.
     *
     * @param message of type String
     */
    public WebhookNotFoundException(String message) {
        super(LogLevelEnum.WARN, "Webhook with " + message + " not found!");
    }
}
