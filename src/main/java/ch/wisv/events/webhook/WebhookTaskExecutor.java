package ch.wisv.events.webhook;

import java.io.IOException;
import java.util.Base64;
import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

/**
 * WebhookTaskExecutor.
 */
@Log
final class WebhookTaskExecutor {

    /**
     * Disabled WebhookTaskExecutor constructor.
     */
    private WebhookTaskExecutor() {
    }

    /**
     * Method sendRequest sends out the sendRequest to a url.
     *
     * @param payloadUrl of type String
     * @param secret     of type String
     * @param body       of type JSONObject
     */
    static void sendRequest(String payloadUrl, String secret, JSONObject body) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(payloadUrl);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString(("CH events:" + secret).getBytes())
        );
        httpPost.setEntity(new StringEntity(body.toJSONString(), "UTF8"));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.severe(String.format("FAILED %s #%d: %s", payloadUrl, response.getStatusLine().getStatusCode(), responseBody));
            }
        } catch (IOException e) {
            log.severe(String.format("FAILED %s %s", payloadUrl, e.getMessage()));
        }
    }
}
