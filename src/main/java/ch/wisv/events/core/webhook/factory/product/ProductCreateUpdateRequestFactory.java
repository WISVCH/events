package ch.wisv.events.core.webhook.factory.product;

import ch.wisv.events.core.exception.runtime.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import org.json.simple.JSONObject;

public class ProductCreateUpdateRequestFactory extends WebhookRequestFactory {

    /**
     * Method getRequestData ...
     *
     * @param object of type Object
     */
    @Override
    public JSONObject getRequestData(Object object) throws WebhookRequestObjectIncorrect {
        if (object instanceof Product) {
            Product product = (Product) object;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", product.getKey());
            jsonObject.put("title", product.getTitle());
            jsonObject.put("description", product.getDescription());
            jsonObject.put("price", product.getCost());
            jsonObject.put("organizedBy", "BESTUUR"); // TODO: https://github.com/WISVCH/events/issues/153

            return jsonObject;
        } else {
            throw new WebhookRequestObjectIncorrect();
        }
    }
}
