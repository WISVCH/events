package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.WebhookTaskRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 * WebhookTaskServiceImpl class.
 */
@Service
@Slf4j
public class WebhookTaskServiceImpl implements WebhookTaskService {

    /** WebhookTaskRepository. */
    private final WebhookTaskRepository webhookTaskRepository;

    /**
     * Constructor WebhookTaskServiceImpl creates a new WebhookTaskServiceImpl instance.
     *
     * @param webhookTaskRepository of type WebhookTaskRepository
     */
    public WebhookTaskServiceImpl(WebhookTaskRepository webhookTaskRepository) {
        this.webhookTaskRepository = webhookTaskRepository;
    }

    /**
     * Method getAll returns the all of this WebhookTaskService object.
     *
     * @return List of WebhookTask.
     */
    @Override
    public List<WebhookTask> getAll() {
        return webhookTaskRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Method create WebhookTask.
     *
     * @param webhookTask of type WebhookTask
     */
    @Override
    public void create(WebhookTask webhookTask) {
        webhookTaskRepository.saveAndFlush(webhookTask);
        log.info("Created WebhookTask #" + webhookTask.getId() + ": " + webhookTask.toString());
    }

    /**
     * Method create ...
     *
     * @param webhookTrigger of type WebhookTrigger
     * @param jsonObject     of type Object
     */
    @Override
    public void create(WebhookTrigger webhookTrigger, Webhook webhook, JSONObject jsonObject) {
        WebhookTask webhookTask = new WebhookTask();
        webhookTask.setTrigger(webhookTrigger);
        webhookTask.setWebhook(webhook);
        webhookTask.setObject(jsonObject);

        this.create(webhookTask);
    }
}
