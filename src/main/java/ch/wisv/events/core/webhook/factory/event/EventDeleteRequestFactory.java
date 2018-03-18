package ch.wisv.events.core.webhook.factory.event;

import ch.wisv.events.core.exception.runtime.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import org.json.simple.JSONObject;

public class EventDeleteRequestFactory extends WebhookRequestFactory {

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

            return jsonObject;
        } else {
            throw new WebhookRequestObjectIncorrect();
        }
    }
}
