package ch.wisv.events.core.webhook.factory.product;

import ch.wisv.events.core.exception.runtime.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import org.json.simple.JSONObject;

public class ProductDeleteFactory extends WebhookRequestFactory {

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

            return jsonObject;
        } else {
            throw new WebhookRequestObjectIncorrect();
        }
    }
}
