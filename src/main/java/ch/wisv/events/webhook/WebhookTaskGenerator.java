package ch.wisv.events.webhook;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.user.LdapGroup;
import static ch.wisv.events.domain.model.user.LdapGroup.CHBEHEER;
import static ch.wisv.events.domain.model.user.LdapGroup.W3CIE;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import ch.wisv.events.services.WebhookService;
import ch.wisv.events.webhook.factory.AbstractWebhookRequestFactory;
import ch.wisv.events.webhook.factory.WebhookFactoryProducer;
import static java.util.Objects.nonNull;
import lombok.extern.java.Log;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebhookPublisher class.
 */
@Component
@Log
public class WebhookTaskGenerator {


    /**
     * WebhookService.
     */
    private final WebhookService webhookService;

    /**
     * Constructor WebhookPublisher creates a new WebhookPublisher instance.
     *
     * @param webhookService     of type WebhookService
     */
    @Autowired
    public WebhookTaskGenerator(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Create all needed Webhook tasks for a certain trigger and model.
     *
     * @param webhookEvent of type WebhookEvent.
     * @param model        of type AbstractModel.
     */
    public void createWebhookTask(WebhookEvent webhookEvent, AbstractModel model) {
        AbstractWebhookRequestFactory factory = WebhookFactoryProducer.getFactory(model);

        if (nonNull(factory)) {
            JSONObject body = factory.generateRequestBody(webhookEvent, model);

            webhookService.getAllByEvent(webhookEvent).stream()
                    .filter(webhook -> this.isWebhookAuthenticated(webhook.getAuthLdapGroup(), model))
                    .forEach(webhook -> WebhookTaskExecutor.sendRequest(webhook.getPayloadUrl(), webhook.getSecret(), body));
        }
    }

    /**
     * Check if a webhook is authenticated to receive the given update.
     *
     * @param ldapGroup of type LdapGroup.
     * @param content   of type AbstractModel.
     *
     * @return boolean
     */
    private boolean isWebhookAuthenticated(LdapGroup ldapGroup, AbstractModel content) {
        if (ldapGroup == CHBEHEER || ldapGroup == W3CIE) {
            return true;
        } else {
            if (content instanceof Event) {
                Event event = (Event) content;

                return event.getOrganizedBy() == ldapGroup;
            } else if (content instanceof Product) {
                Product product = (Product) content;

                return product.getEvent().getOrganizedBy() == ldapGroup;
            }
        }

        return false;
    }
}
