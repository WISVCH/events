package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookTaskRepository extends JpaRepository<WebhookTask, Integer> {

    /**
     * Method findAllOrderBy_CreatedAt_Asc ...
     *
     * @return List<WebhookTask>
     */
    List<WebhookTask> findAllByOrderByCreatedAtAsc();

    /**
     * Method findAllByWebhookTaskStatus().
     *
     * @param webhookTaskStatus of type WebhookTaskStatus
     *
     * @return List<WebhookTask>
     */
    List<WebhookTask> findAllByWebhookTaskStatus(WebhookTaskStatus webhookTaskStatus);

}
