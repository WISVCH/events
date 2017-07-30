package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.WebhookRepository;
import ch.wisv.events.utils.LDAPGroup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
@Profile("dev")
@Order(value = 5)
public class WebhookTestDataRunner extends TestDataRunner {

    /**
     * Field eventRepository
     */
    private final WebhookRepository webhookRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param webhookRepository of type WebhookRepository
     */
    public WebhookTestDataRunner(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;

        this.setJsonFileName("webhooks.json");
    }

    /**
     * Method loop
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Webhook webhook = this.createWebhook(jsonObject);

        this.webhookRepository.save(webhook);
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     * @return Product
     */
    private Webhook createWebhook(JSONObject jsonObject) {
        Webhook webhook = new Webhook();
        webhook.setLdapGroup(LDAPGroup.valueOf((String) jsonObject.get("ldapGroup")));
        webhook.setPayloadUrl((String) jsonObject.get("payloadUrl"));
        webhook.setActive(true);

        return this.addTriggers(webhook, (JSONArray) jsonObject.get("triggers"));
    }

    /**
     * Method addTriggers.
     *
     * @param webhook of type Webhook
     * @param jsonArray of type JSONArray
     * @return Webhook
     */
    private Webhook addTriggers(Webhook webhook, JSONArray jsonArray) {
        List<WebhookTrigger> triggers = new ArrayList<>();
        for (Object o : jsonArray) {
            triggers.add(WebhookTrigger.valueOf((String) o));
        }
        webhook.setWebhookTriggers(triggers);

        return webhook;
    }
}
