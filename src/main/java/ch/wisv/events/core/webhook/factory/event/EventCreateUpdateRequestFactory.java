package ch.wisv.events.core.webhook.factory.event;

import ch.wisv.events.core.exception.runtime.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventCategory;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import ch.wisv.events.core.webhook.factory.product.ProductCreateUpdateRequestFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EventCreateUpdateRequestFactory extends WebhookRequestFactory {

    /**
     * Method getRequestData ...
     *
     * @param object of type Object
     */
    @Override
    public JSONObject getRequestData(Object object) throws WebhookRequestObjectIncorrect {
        if (object instanceof Event) {
            Event event = (Event) object;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", event.getKey());
            jsonObject.put("title", event.getTitle());
            jsonObject.put("description", event.getDescription());
            jsonObject.put("short_description", event.getShortDescription());
            jsonObject.put("event_start", event.getStart().toString());
            jsonObject.put("event_end", event.getEnding().toString());
            jsonObject.put("location", event.getLocation());
            jsonObject.put("categories", event.getCategories().stream().map(EventCategory::toString).collect(Collectors.toList()));
            jsonObject.put("products", this.generateProductRequestData(event.getProducts()));

            return jsonObject;
        } else {
            throw new WebhookRequestObjectIncorrect();
        }
    }

    /**
     * Method generateProductRequestData ...
     *
     * @param productList of type List of Products
     *
     * @return JSONArray
     */
    private JSONArray generateProductRequestData(List<Product> productList) {
        ProductCreateUpdateRequestFactory productCreateUpdateRequestFactory = new ProductCreateUpdateRequestFactory();
        JSONArray jsonArray = new JSONArray();

        productList.forEach(product -> {
            try {
                jsonArray.add(productCreateUpdateRequestFactory.getRequestData(product));
            } catch (WebhookRequestObjectIncorrect webhookRequestObjectIncorrect) {
                webhookRequestObjectIncorrect.printStackTrace();
            }
        });

        return jsonArray;
    }
}
