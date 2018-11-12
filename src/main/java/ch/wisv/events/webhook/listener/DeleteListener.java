package ch.wisv.events.webhook.listener;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import ch.wisv.events.services.WebhookService;
import ch.wisv.events.webhook.event.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * EventCreateUpdateListener.
 */
@Component
public class DeleteListener extends AbstractListener<Delete> {

    /**
     * CreateUpdateListener constructor.
     *
     * @param webhookService     of type WebhookService
     */
    @Autowired
    public DeleteListener(WebhookService webhookService) {
        super(webhookService);
    }

    /**
     * Get WebhookEvent based on the AbstractModel.
     *
     * @param eventModel of type AbstractModel
     *
     * @return WebhookEvent
     */
    @Override
    protected WebhookEvent getWebhookEvent(AbstractModel eventModel) {
        WebhookEvent webhookEvent = null;
        if (Event.class.isAssignableFrom(eventModel.getClass())) {
            webhookEvent = WebhookEvent.EVENT_DELETE;
        } else if (Product.class.isAssignableFrom(eventModel.getClass())) {
            webhookEvent = WebhookEvent.PRODUCT_DELETE;
        }

        return webhookEvent;
    }

}
