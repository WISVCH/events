package ch.wisv.events.webhook.factory;

import ch.wisv.events.domain.model.event.EventCategory;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;

/**
 * EventWebhookRequestFactory.
 */
public class ProductWebhookRequestFactory extends AbstractWebhookRequestFactory<Product> {

    /**
     * Generate request body.
     *
     * @param event of type WebhookEvent
     * @param model of type AbstractModel
     *
     * @return JSONObject
     */
    @Override
    public JSONObject generateRequestBody(WebhookEvent event, Product model) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("trigger", event.getTag());
        jsonObject.put("key", model.getPublicReference());

        if (event == WebhookEvent.PRODUCT_CREATE_EDIT) {
            jsonObject.put("title", model.getTitle());
            jsonObject.put("description", model.getDescription());
            jsonObject.put("price", model.getPrice());
            jsonObject.put("organizedBy", model.getEvent().getOrganizedBy());
        }

        return jsonObject;
    }
}
