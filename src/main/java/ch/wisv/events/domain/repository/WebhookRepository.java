package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.webhook.Webhook;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * WebhookRepository.
 */
@Repository
public interface WebhookRepository extends AbstractRepository<Webhook> {

    /**
     * Find all by an given WebhookEvent and which is active.
     *
     * @param webhookEvent of type WebhookEvent
     *
     * @return List
     */
    List<Webhook> findAllByEventsContainsAndActiveIsTrue(WebhookEvent webhookEvent);
}
