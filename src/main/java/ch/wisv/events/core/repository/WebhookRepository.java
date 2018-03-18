package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, Integer> {

    /**
     * Method findByKey find Vendor by Key.
     *
     * @param key of type String
     *
     * @return Optional<Vendor>
     */
    Optional<Webhook> findByKey(String key);

    /**
     * Method findAllByWebhookTriggersContainsAndLdapGroup ...
     *
     * @param webhookTrigger of type WebhookTrigger
     *
     * @return List<Webhook>
     */
    List<Webhook> findAllByWebhookTriggersIsContaining(WebhookTrigger webhookTrigger);
}
