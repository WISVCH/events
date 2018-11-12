package ch.wisv.events.webhook.factory;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import org.json.simple.JSONObject;

/**
 * AbstractWebhookRequestFactory.
 *
 * @param <T> of type AbstractModel
 */
public abstract class AbstractWebhookRequestFactory<T extends AbstractModel> {

    /**
     * Generate request body.
     *
     * @param event of type WebhookEvent
     * @param model of type AbstractModel
     *
     * @return JSONObject
     */
    public abstract JSONObject generateRequestBody(WebhookEvent event, T model);
}
