package ch.wisv.events.webhook.listener;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import ch.wisv.events.services.WebhookService;
import ch.wisv.events.services.WebhookTaskService;
import ch.wisv.events.webhook.event.CreateUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * EventCreateUpdateListener.
 */
@Component
public class CreateUpdateListener extends AbstractListener<CreateUpdate> {

    /**
     * CreateUpdateListener constructor.
     *
     * @param webhookService     of type WebhookService
     * @param webhookTaskService of type WebhookTaskService
     */
    @Autowired
    public CreateUpdateListener(WebhookService webhookService, WebhookTaskService webhookTaskService) {
        super(webhookService, webhookTaskService);
    }

    /**
     * Get WebhookEvent based on the AbstractModel.
     *
     * @param eventModel of type AbstractModel
     *
     * @return WebhookEvent
     */
    protected WebhookEvent getWebhookEvent(AbstractModel eventModel) {
        WebhookEvent webhookEvent = null;
        if (Event.class.isAssignableFrom(eventModel.getClass())) {
            webhookEvent = WebhookEvent.EVENT_CREATE_EDIT;
        } else if (Product.class.isAssignableFrom(eventModel.getClass())) {
            webhookEvent = WebhookEvent.PRODUCT_CREATE_EDIT;
        }

        return webhookEvent;
    }

}
