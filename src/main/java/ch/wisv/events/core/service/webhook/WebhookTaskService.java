package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import java.util.List;
import org.json.simple.JSONObject;

public interface WebhookTaskService {

    /**
     * Method getAll returns the all of this WebhookTaskService object.
     *
     * @return the all (type List<WebhookTask>) of this WebhookTaskService object.
     */
    List<WebhookTask> getAll();

    /**
     * Method create WebhookTask
     *
     * @param webhookTask of type WebhookTask
     */
    void create(WebhookTask webhookTask);

    /**
     * Method create ...
     *
     * @param webhookTrigger of type WebhookTrigger
     * @param jsonObject     of type Object
     */
    void create(WebhookTrigger webhookTrigger, Webhook webhook, JSONObject jsonObject);

}
