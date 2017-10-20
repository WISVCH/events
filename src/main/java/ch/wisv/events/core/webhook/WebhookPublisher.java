package ch.wisv.events.core.webhook;

import ch.wisv.events.core.exception.WebhookRequestFactoryNotFoundException;
import ch.wisv.events.core.exception.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.webhook.WebhookService;
import ch.wisv.events.core.service.webhook.WebhookTaskService;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import ch.wisv.events.utils.LDAPGroup;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class WebhookPublisher {

    /**
     * Field webhookService
     */
    private final WebhookService webhookService;

    /**
     * Field webhookTaskService
     */
    private final WebhookTaskService webhookTaskService;

    private final EventService productService;

    /**
     * Constructor WebhookPublisher creates a new WebhookPublisher instance.
     *
     * @param webhookService     of type WebhookService.
     * @param webhookTaskService of type WebhookTaskService.
     * @param productService
     */
    @Autowired
    private WebhookPublisher(WebhookService webhookService,
            WebhookTaskService webhookTaskService,
            EventService productService
    ) {
        this.webhookService = webhookService;
        this.webhookTaskService = webhookTaskService;
        this.productService = productService;
    }

    /**
     * Method createWebhookTask ...
     *
     * @param webhookTrigger of type WebhookTrigger
     * @param object         of type Object
     */
    public void createWebhookTask(WebhookTrigger webhookTrigger, Object object) {
        try {
            JSONObject jsonObject = WebhookRequestFactory.generateRequest(webhookTrigger, object);

            webhookService.getByTrigger(webhookTrigger).forEach(webhook -> {
                if (this.isWebhookAuthenticated(object, webhook)) {
                    webhookTaskService.create(webhookTrigger, webhook, jsonObject);
                }
            });
        } catch (WebhookRequestFactoryNotFoundException | WebhookRequestObjectIncorrect e) {
            e.printStackTrace();
        }
    }

    /**
     * Method isWebhookAuthenticated ...
     *
     * @param object  of type Object
     * @param webhook of type Webhook
     * @return boolean
     */
    private boolean isWebhookAuthenticated(Object object, Webhook webhook) {
        if (webhook.getLdapGroup() == LDAPGroup.BEHEER) {
            return true;
        } else if (object instanceof Event) {
            if (((Event) object).getOrganizedBy() == webhook.getLdapGroup()) {
                return true;
            }
        } else if (object instanceof Product) {
            if (productService.getEventByProduct((Product) object).getOrganizedBy() == webhook.getLdapGroup()) {
                return true;
            }
        }

        return false;
    }
}
