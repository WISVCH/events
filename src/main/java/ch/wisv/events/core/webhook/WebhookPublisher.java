package ch.wisv.events.core.webhook;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.runtime.WebhookRequestFactoryNotFoundException;
import ch.wisv.events.core.exception.runtime.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.webhook.WebhookService;
import ch.wisv.events.core.service.webhook.WebhookTaskService;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebhookPublisher class.
 */
@Component
public class WebhookPublisher {

    /** WebhookService. */
    private final WebhookService webhookService;

    /** WebhookTaskService. */
    private final WebhookTaskService webhookTaskService;

    /** EventService. */
    private final EventService eventService;

    /**
     * Constructor WebhookPublisher creates a new WebhookPublisher instance.
     *
     * @param webhookService     of type WebhookService.
     * @param webhookTaskService of type WebhookTaskService.
     * @param eventService       of type EventService.
     */
    @Autowired
    private WebhookPublisher(
            WebhookService webhookService, WebhookTaskService webhookTaskService, EventService eventService
    ) {
        this.webhookService = webhookService;
        this.webhookTaskService = webhookTaskService;
        this.eventService = eventService;
    }

    /**
     * Create all needed Webhook tasks for a certain trigger and content.
     *
     * @param webhookTrigger of type WebhookTrigger.
     * @param content        of type Object.
     */
    public void createWebhookTask(WebhookTrigger webhookTrigger, Object content) {
        try {
            JSONObject jsonObject = WebhookRequestFactory.generateRequest(webhookTrigger, content);

            webhookService.getByTrigger(webhookTrigger).forEach(webhook -> {
                if (this.isWebhookAuthenticated(webhook, content)) {
                    webhookTaskService.create(webhookTrigger, webhook, jsonObject);
                }
            });
        } catch (WebhookRequestFactoryNotFoundException | WebhookRequestObjectIncorrect ignored) {
        }
    }

    /**
     * Check if a webhook is authenticated to receive the given update.
     *
     * @param webhook of type Webhook.
     * @param content of type Object.
     *
     * @return boolean
     */
    private boolean isWebhookAuthenticated(Webhook webhook, Object content) {
        if (webhook.getLdapGroup() == ch.wisv.events.utils.LdapGroup.BEHEER) {
            return true;
        } else {
            if (content instanceof Event event) {

                return webhook.getLdapGroup() == event.getOrganizedBy();
            } else if (content instanceof Product product) {

                try {
                    Event event = eventService.getByProduct(product);

                    if (event.getOrganizedBy() == webhook.getLdapGroup()) {
                        return true;
                    }
                } catch (EventNotFoundException ignored) {
                }
            }
        }

        return false;
    }
}
