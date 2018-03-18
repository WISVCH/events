package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * WebhookTaskRepository interface.
 */
public interface WebhookTaskRepository extends JpaRepository<WebhookTask, Integer> {

    /**
     * Method findAllOrderBy_CreatedAt_Asc ...
     *
     * @return List
     */
    List<WebhookTask> findAllByOrderByCreatedAtAsc();

    /**
     * Method findAllByWebhookTaskStatus().
     *
     * @param webhookTaskStatus of type WebhookTaskStatus
     *
     * @return List
     */
    List<WebhookTask> findAllByWebhookTaskStatus(WebhookTaskStatus webhookTaskStatus);

}