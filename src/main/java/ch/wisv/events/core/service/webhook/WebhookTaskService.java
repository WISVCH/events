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
     * @return List of WebhookTasks
     */
    List<WebhookTask> getAll();

    /**
     * Method create WebhookTask.
     *
     * @param webhookTask of type WebhookTask
     */
    void create(WebhookTask webhookTask);

    /**
     * Create webhook task by trigger, webhook and jsonObject.
     *
     * @param webhookTrigger of type WebhookTrigger
     * @param webhook        of type Webhook
     * @param jsonObject     of type Object
     */
    void create(WebhookTrigger webhookTrigger, Webhook webhook, JSONObject jsonObject);

}
