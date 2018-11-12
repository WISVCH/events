package ch.wisv.events.webhook.listener;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import ch.wisv.events.services.WebhookService;
import ch.wisv.events.webhook.WebhookTaskGenerator;
import static java.util.Objects.nonNull;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * EventCreateUpdateListener.
 *
 * @param <T> of type ApplicationEvent
 */
abstract class AbstractListener<T extends ApplicationEvent> implements ApplicationListener<T> {

    /**
     * WebhookService.
     */
    private final WebhookService webhookService;

    /**
     * AbstractListener constructor.
     *
     * @param webhookService     of type WebhookService
     */
    AbstractListener(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(T event) {
        WebhookTaskGenerator generator = new WebhookTaskGenerator(webhookService);
        AbstractModel eventModel = (AbstractModel) event.getSource();

        WebhookEvent webhookEvent = this.getWebhookEvent(eventModel);

        if (nonNull(webhookEvent)) {
            generator.createWebhookTask(webhookEvent, eventModel);
        }
    }

    /**
     * Get WebhookEvent based on the AbstractModel.
     *
     * @param eventModel of type AbstractModel
     *
     * @return WebhookEvent
     */
    protected abstract WebhookEvent getWebhookEvent(AbstractModel eventModel);

}
