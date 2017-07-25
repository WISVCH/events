package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.model.webhook.Webhook;

import java.util.List;

/**
 * Copyright (c) 2017 Can i spend it
 *
 * @author svenp
 * @since 20170725
 */
public interface WebhookService {

    /**
     * Method getAll returns the all of this WebhookService object.
     *
     * @return the all (type List<Webhook>) of this WebhookService object.
     */
    public List<Webhook> getAll();



}
