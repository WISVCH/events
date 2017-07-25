package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.exception.InvalidWebhookException;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright (c) 2017 Can i spend it
 *
 * @author svenp
 * @since 20170725
 */
@Service
public class WebhookServiceImpl implements WebhookService {

    /**
     * Field repository
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
     * @return the all (type List<Webhook>) of this WebhookService object.
     */
    @Override
    public List<Webhook> getAll() {
        return repository.findAll();
    }

    /**
     * Method create ...
     *
     * @param model of type Webhook
     */
    @Override
    public void create(Webhook model) throws InvalidWebhookException {
        if (model.getPayloadUrl() == null || model.getPayloadUrl().equals("")) {
            throw new InvalidWebhookException();
        } else {
            repository.saveAndFlush(model);
        }
    }
}
