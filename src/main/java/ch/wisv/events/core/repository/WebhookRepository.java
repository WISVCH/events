package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.utils.LDAPGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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
public interface WebhookRepository extends JpaRepository<Webhook, Integer> {

    /**
     * Method findByKey find Vendor by Key.
     *
     * @param key of type String
     * @return Optional<Vendor>
     */
    Optional<Webhook> findByKey(String key);

    /**
     * Method findAllByWebhookTriggersContainsAndLdapGroup ...
     *
     * @param webhookTrigger of type WebhookTrigger
     * @return List<Webhook>
     */
    List<Webhook> findAllByWebhookTriggersContainsAndLdapGroup(WebhookTrigger webhookTrigger, LDAPGroup ldapGroup);
}
