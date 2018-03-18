package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class WebhookRequestFactoryNotFoundException extends EventsRuntimeException {

    /**
     * WebhookRequestFactoryNotFoundException exception.
     */
    public WebhookRequestFactoryNotFoundException() {
        super(LogLevelEnum.WARN, "Webhook request factory has not been found!");
    }
}
