package ch.wisv.events.webhook.factory;

import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.event.EventCategory;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * EventWebhookRequestFactory.
 */
public class EventWebhookRequestFactory extends AbstractWebhookRequestFactory<Event> {

    /**
     * Generate request body.
     *
     * @param event of type WebhookEvent
     * @param model of type AbstractModel
     *
     * @return JSONObject
     */
    @Override
    public JSONObject generateRequestBody(WebhookEvent event, Event model) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("trigger", event.getTag());
        jsonObject.put("key", model.getPublicReference());

        if (event == WebhookEvent.EVENT_CREATE_EDIT) {
            jsonObject.put("title", model.getTitle());
            jsonObject.put("description", model.getDescription());
            jsonObject.put("short_description", model.getShortDescription());
            jsonObject.put("event_start", model.getStarting().toString());
            jsonObject.put("event_end", model.getEnding().toString());
            jsonObject.put("location", model.getLocation());
            jsonObject.put("categories", model.getCategories().stream().map(EventCategory::toString).collect(Collectors.toList()));
            jsonObject.put("products", this.generateProductRequestBody(model.getProducts()));
        }

        return jsonObject;
    }

    /**
     * Generate product request body.
     *
     * @param products of type List
     *
     * @return JSONArray
     */
    private JSONArray generateProductRequestBody(List<Product> products) {
        ProductWebhookRequestFactory productWebhookRequestFactory = new ProductWebhookRequestFactory();
        JSONArray jsonArray = new JSONArray();

        products.forEach(product -> jsonArray.add(productWebhookRequestFactory.generateRequestBody(WebhookEvent.PRODUCT_CREATE_EDIT, product)));

        return jsonArray;
    }
}
