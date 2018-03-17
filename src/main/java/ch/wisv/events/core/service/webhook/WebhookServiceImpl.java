package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.exception.normal.WebhookInvalidException;
import ch.wisv.events.core.exception.normal.WebhookNotFoundException;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Service
public class WebhookServiceImpl implements WebhookService {

    /**
     * Field this.repository
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
        return this.repository.findAll();
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
        Optional<Webhook> webhookOptional = this.repository.findByKey(key);

        return webhookOptional.orElseThrow(() -> new WebhookNotFoundException("key " + key));
    }

    /**
     * Method getByTriggerAndLdapGroup ...
     *
     * @param webhookTrigger of type WebhookTrigger
     *
     * @return List<Webhook>
     */
    @Override
    public List<Webhook> getByTrigger(WebhookTrigger webhookTrigger) {
        return this.repository.findAllByWebhookTriggersIsContaining(webhookTrigger);
    }

    /**
     * Method create a new Webhook.
     *
     * @param model of type Webhook
     */
    @Override
    public void create(Webhook model) throws WebhookInvalidException {
        this.assertIsValidWebhook(model);

        this.repository.saveAndFlush(model);
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

        this.repository.saveAndFlush(webhook);
    }

    /**
     * Method delete an existing Webhook.
     *
     * @param model of type Webhook
     */
    @Override
    public void delete(Webhook model) {
        this.repository.delete(model);
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
    }
}
