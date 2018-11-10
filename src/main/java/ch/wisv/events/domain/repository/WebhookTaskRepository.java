package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.webhook.WebhookTask;
import org.springframework.stereotype.Repository;

/**
 * WebhookTaskRepository.
 */
@Repository
public interface WebhookTaskRepository extends AbstractRepository<WebhookTask> {

}
