package ch.wisv.events.core.webhook;

import ch.wisv.events.core.exception.WebhookRequestFactoryNotFoundException;
import ch.wisv.events.core.exception.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.webhook.WebhookService;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
public class WebhookPublisher extends Thread {

    /**
     * Field webhookService
     */
    private final WebhookService webhookService;

    /**
     * Field trigger
     */
    @Setter
    private WebhookTrigger trigger;

    /**
     * Field object
     */
    @Setter
    private Object object;

    /**
     * Constructor WebhookPublisher creates a new WebhookPublisher instance.
     *
     * @param webhookService of type WebhookService.
     */
    private WebhookPublisher(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Method event set up an event.
     *
     * @param trigger of type WebhookTrigger
     * @param object  of type Object
     */
    public void event(WebhookTrigger trigger, Object object) {
        this.setTrigger(trigger);
        this.setObject(object);

        this.run();
    }

    /**
     * Method run generates request and send requests to all the webhooks.
     */
    public void run() {
        try {
            JSONObject request = WebhookRequestFactory.generateRequest(this.trigger, this.object);

            List<Webhook> webhooks = this.webhookService.getByTrigger(trigger);
            webhooks.forEach(webhook -> {
                this.request(webhook.getPayloadUrl(), request);
            });
        } catch (WebhookRequestFactoryNotFoundException | WebhookRequestObjectIncorrect e) {
            e.printStackTrace();
        }
    }

    /**
     * Method request sends out the request to a url.
     *
     * @param url     of type String
     * @param request of type JSONObject
     */
    private void request(String url, JSONObject request) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(request.toJSONString(), "UTF8"));

        try {
            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // TODO: add monitoring
            }
        } catch (IOException e) {
            // TODO: add monitoring
        }
    }
}
