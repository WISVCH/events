package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.exception.normal.WebhookInvalidException;
import ch.wisv.events.core.exception.normal.WebhookNotFoundException;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import java.util.List;

public interface WebhookService {

    /**
     * Method getAll returns the all of this WebhookService object.
     *
     * @return the all (type List<Webhook>) of this WebhookService object.
     */
    List<Webhook> getAll();

    /**
     * Method getByKey get Webhook by Key.
     *
     * @param key of type String
     *
     * @return Webhook
     */
    Webhook getByKey(String key) throws WebhookNotFoundException;

    /**
     * Method getByTriggerAndLdapGroup ...
     *
     * @param webhookTrigger of type WebhookTrigger
     *
     * @return List<Webhook>
     */
    List<Webhook> getByTrigger(WebhookTrigger webhookTrigger);

    /**
     * Method create a new Webhook.
     *
     * @param model of type Webhook
     */
    void create(Webhook model) throws WebhookInvalidException;

    /**
     * Method update an existing Webhook.
     *
     * @param model of type Webhook
     */
    void update(Webhook model) throws WebhookNotFoundException, WebhookInvalidException;

    /**
     * Method delete an existing Webhook.
     *
     * @param model of type Webhook
     */
    void delete(Webhook model);
}
