package ch.wisv.events.core.webhook;

import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTaskStatus;
import ch.wisv.events.core.repository.WebhookTaskRepository;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookTaskScheduler {

    /** Task interval in seconds. */
    private static final int WEBHOOK_TASK_INTERVAL_SECONDS = 60;

    /** WebhookTaskRepository. */
    private final WebhookTaskRepository webhookTaskRepository;

    /**
     * Constructor WebhookTaskScheduler creates a new WebhookTaskScheduler instance.
     *
     * @param webhookTaskRepository of type WebhookTaskRepository
     */
    @Autowired
    public WebhookTaskScheduler(WebhookTaskRepository webhookTaskRepository) {
        this.webhookTaskRepository = webhookTaskRepository;
    }

    /**
     * Method webhookTask ...
     */
    @Scheduled(fixedRate = WEBHOOK_TASK_INTERVAL_SECONDS * 1000)
    public void webhookTask() {
        List<WebhookTask> webhookTaskList = this.webhookTaskRepository.findAllByWebhookTaskStatus(WebhookTaskStatus.PENDING);

        webhookTaskList.forEach(webhookTask -> {
            log.info("Starting WebhookTask #" + webhookTask.getId() + ": " + webhookTask.toString());
            this.sendRequest(webhookTask);
        });
    }

    /**
     * Method sendRequest sends out the sendRequest to a url.
     *
     * @param webhookTask of type Webhook
     */
    private void sendRequest(WebhookTask webhookTask) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(webhookTask.getWebhook().getPayloadUrl());

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString(("CH events:" + webhookTask.getWebhook().getSecret()).getBytes())
        );
        httpPost.setEntity(new StringEntity(webhookTask.getObject().toJSONString(), "UTF8"));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                webhookTask.setWebhookTaskStatus(WebhookTaskStatus.SUCCESS);
            } else {
                log.error("ERROR Task #" + webhookTask.getId() + ": " + responseBody);
                webhookTask.setWebhookTaskStatus(WebhookTaskStatus.ERROR);
                webhookTask.setWebhookError(responseBody);
            }
        } catch (IOException e) {
            log.error("IOException Task #" + webhookTask.getId() + ": " + e.getMessage());
            webhookTask.setWebhookTaskStatus(WebhookTaskStatus.ERROR);
            webhookTask.setWebhookError(e.getMessage());
        }

        log.info("Finished WebhookTask #" + webhookTask.getId());
        this.webhookTaskRepository.save(webhookTask);
    }
}
