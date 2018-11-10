package ch.wisv.events.webhook.factory;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import org.json.simple.JSONObject;

/**
 * EventWebhookRequestFactory.
 */
public class EventWebhookRequestFactory extends AbstractWebhookRequestFactory {

    /**
     * Generate request body.
     *
     * @param event of type WebhookEvent
     * @param model of type AbstractModel
     *
     * @return JSONObject
     */
    @Override
    public JSONObject generateRequestBody(WebhookEvent event, AbstractModel model) {
        return null;
    }
}
