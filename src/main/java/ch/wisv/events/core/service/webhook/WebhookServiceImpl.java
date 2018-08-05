package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.exception.normal.WebhookInvalidException;
import ch.wisv.events.core.exception.normal.WebhookNotFoundException;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.WebhookRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WebhookService implementation.
 */
@Service
public class WebhookServiceImpl implements WebhookService {

    /**
     * Field repository.
     */
    private final WebhookRepository repository;

    /**
     * Constructor WebhookServiceImpl creates a new WebhookServiceImpl instance.
     *
     * @param repository of type WebhookRepository
     */
    @Autowired
    public WebhookServiceImpl(WebhookRepository repository) {
        this.repository = repository;
    }

    /**
     * Method getAll returns the all of this WebhookService object.
     *
     * @return List of Webhook
     */
    @Override
    public List<Webhook> getAll() {
        return repository.findAll();
    }

    /**
     * Method getByKey get Webhook by Key.
     *
     * @param key of type String
     *
     * @return Webhook
     */
    @Override
    public Webhook getByKey(String key) throws WebhookNotFoundException {
        Optional<Webhook> webhookOptional = repository.findByKey(key);

        return webhookOptional.orElseThrow(() -> new WebhookNotFoundException("key " + key));
    }

    /**
     * Method getByTriggerAndLdapGroup ...
     *
     * @param webhookTrigger of type WebhookTrigger
     *
     * @return List of Webhook
     */
    @Override
    public List<Webhook> getByTrigger(WebhookTrigger webhookTrigger) {
        return repository.findAllByWebhookTriggersIsContaining(webhookTrigger);
    }

    /**
     * Method create a new Webhook.
     *
     * @param model of type Webhook
     */
    @Override
    public void create(Webhook model) throws WebhookInvalidException {
        this.assertIsValidWebhook(model);

        repository.saveAndFlush(model);
    }

    /**
     * Method update an existing Webhook.
     *
     * @param model of type Webhook
     */
    @Override
    public void update(Webhook model) throws WebhookNotFoundException, WebhookInvalidException {
        Webhook webhook = this.getByKey(model.getKey());
        webhook.setPayloadUrl(model.getPayloadUrl());
        webhook.setWebhookTriggers(model.getWebhookTriggers());
        webhook.setActive(model.isActive());
        webhook.setLdapGroup(model.getLdapGroup());

        this.assertIsValidWebhook(webhook);

        repository.saveAndFlush(webhook);
    }

    /**
     * Method delete an existing Webhook.
     *
     * @param model of type Webhook
     */
    @Override
    public void delete(Webhook model) {
        repository.delete(model);
    }

    /**
     * Method assertIsValidWebhook ...
     *
     * @param model of type Webhook
     *
     * @throws WebhookInvalidException when
     */
    private void assertIsValidWebhook(Webhook model) throws WebhookInvalidException {
        if (model.getPayloadUrl() == null || model.getPayloadUrl().equals("")) {
            throw new WebhookInvalidException("Payload URL can not be empty!");
        }

        if (model.getLdapGroup() == null) {
            throw new WebhookInvalidException("LDAP group can not be null!");
        }

        if (model.getWebhookTriggers() == null) {
            throw new WebhookInvalidException("WebhookTriggers can not be null!");
        }
    }
}
