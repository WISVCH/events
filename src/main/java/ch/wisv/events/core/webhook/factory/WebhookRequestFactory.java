package ch.wisv.events.core.webhook.factory;

import ch.wisv.events.core.exception.runtime.WebhookRequestFactoryNotFoundException;
import ch.wisv.events.core.exception.runtime.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.webhook.factory.event.EventCreateUpdateRequestFactory;
import ch.wisv.events.core.webhook.factory.event.EventDeleteRequestFactory;
import ch.wisv.events.core.webhook.factory.product.ProductCreateUpdateRequestFactory;
import ch.wisv.events.core.webhook.factory.product.ProductDeleteFactory;
import java.util.HashMap;
import org.json.simple.JSONObject;

public abstract class WebhookRequestFactory {

    /**
     * Field mapping
     */
    private static HashMap<WebhookTrigger, WebhookRequestFactory> mapping = new HashMap<WebhookTrigger, WebhookRequestFactory>() {{
        put(WebhookTrigger.EVENT_CREATE_UPDATE, new EventCreateUpdateRequestFactory());
        put(WebhookTrigger.EVENT_DELETE, new EventDeleteRequestFactory());
        put(WebhookTrigger.PRODUCT_CREATE_UPDATE, new ProductCreateUpdateRequestFactory());
        put(WebhookTrigger.PRODUCT_DELETE, new ProductDeleteFactory());
    }};

    /**
     * Method generateRequest ...
     *
     * @param trigger of type WebhookTrigger
     * @param object  of type Object
     *
     * @return JSONObject
     */
    public static JSONObject generateRequest(WebhookTrigger trigger, Object object)
            throws WebhookRequestFactoryNotFoundException, WebhookRequestObjectIncorrect {
        WebhookRequestFactory factory = determineFactory(trigger);
        JSONObject request = factory.getRequestData(object);
        request.put("trigger", trigger.getTag());

        return request;
    }

    /**
     * Method determineFactory ...
     *
     * @param trigger of type WebhookTrigger
     *
     * @return WebhookRequestFactory
     *
     * @throws WebhookRequestFactoryNotFoundException when
     */
    private static WebhookRequestFactory determineFactory(WebhookTrigger trigger) throws WebhookRequestFactoryNotFoundException {
        if (mapping.containsKey(trigger)) {
            return mapping.get(trigger);
        } else {
            throw new WebhookRequestFactoryNotFoundException();
        }
    }

    /**
     * Method getRequestData ...
     *
     * @param object of type Object
     */
    public abstract JSONObject getRequestData(Object object) throws WebhookRequestObjectIncorrect;
}
