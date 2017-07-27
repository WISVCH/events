package ch.wisv.events.core.webhook;

import ch.wisv.events.core.exception.WebhookRequestFactoryNotFoundException;
import ch.wisv.events.core.exception.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

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
public class WebhookPublisher {

    protected WebhookPublisher() {

    }

    public static void event(WebhookTrigger trigger, Object object) {
        WebhookPublisher webhookPusher = new WebhookPublisher();

        try {
            JSONObject request = WebhookRequestFactory.generateRequest(trigger, object);
            webhookPusher.request("http://httpbin.org/post", request);
        } catch (WebhookRequestFactoryNotFoundException | UnsupportedEncodingException | WebhookRequestObjectIncorrect e) {
            e.printStackTrace();
        }
    }

    protected void request(String url, JSONObject request) throws UnsupportedEncodingException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setEntity(new StringEntity(request.toString()));
        httpPost.setHeader("Content-type", "application/json");

        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            InputStream in = entity.getContent();
            Scanner sc = new Scanner(in);

            while (sc.hasNext()) {
                System.out.println(sc.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
