package ch.wisv.events.webhook.factory;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import org.json.simple.JSONObject;

/**
 * AbstractWebhookRequestFactory.
 */
public abstract class AbstractWebhookRequestFactory {

    /**
     * Generate request body.
     *
     * @param event of type WebhookEvent
     * @param model of type AbstractModel
     *
     * @return JSONObject
     */
    public abstract JSONObject generateRequestBody(WebhookEvent event, AbstractModel model);
}
