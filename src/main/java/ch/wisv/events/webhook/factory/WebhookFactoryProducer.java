package ch.wisv.events.webhook.factory;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.event.Event;

/**
 * WebhookFactoryProducer.
 */
public final class WebhookFactoryProducer {

    /**
     * WebhookFactoryProducer constructor.
     */
    private WebhookFactoryProducer() {
    }

    /**
     * Get a Factory based on a Abstract model.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractWebhookRequestFactory
     */
    public static AbstractWebhookRequestFactory getFactory(AbstractModel model) {
        if (Event.class.isAssignableFrom(model.getClass())) {
            return new EventWebhookRequestFactory();
        }

        return null;
    }
}
