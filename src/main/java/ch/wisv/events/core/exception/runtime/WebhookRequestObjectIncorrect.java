package ch.wisv.events.core.exception.runtime;

import ch.wisv.events.core.exception.LogLevelEnum;

public class WebhookRequestObjectIncorrect extends EventsRuntimeException {

    /**
     * WebhookRequestObjectIncorrect exception.
     */
    public WebhookRequestObjectIncorrect() {
        super(LogLevelEnum.WARN, "Webhook request object is incorrect!");
    }
}
