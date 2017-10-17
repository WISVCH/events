package ch.wisv.events.core.webhook.factory;

import ch.wisv.events.core.exception.WebhookRequestFactoryNotFoundException;
import ch.wisv.events.core.exception.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.webhook.factory.event.EventCreateUpdateRequestFactory;
import ch.wisv.events.core.webhook.factory.event.EventDeleteRequestFactory;
import ch.wisv.events.core.webhook.factory.product.ProductCreateUpdateRequestFactory;
import ch.wisv.events.core.webhook.factory.product.ProductDeleteFactory;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
     * @return JSONObject
     */
    public static JSONObject generateRequest(WebhookTrigger trigger,
            Object object
    ) throws WebhookRequestFactoryNotFoundException, WebhookRequestObjectIncorrect {
        WebhookRequestFactory factory = determineFactory(trigger);
        JSONObject request = factory.getRequestData(object);
        request.put("trigger", trigger.getTag());

        return request;
    }

    /**
     * Method determineFactory ...
     *
     * @param trigger of type WebhookTrigger
     * @return WebhookRequestFactory
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
