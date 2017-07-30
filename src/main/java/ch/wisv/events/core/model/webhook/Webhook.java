package ch.wisv.events.core.model.webhook;

import ch.wisv.events.utils.LDAPGroup;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

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
@Entity
@Data
public class Webhook {

    /**
     * ID of the product, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Key of the product, getter only so it can not be changed.
     */
    private String key;

    /**
     * Field payloadUrl.
     */
    private String payloadUrl;

    /**
     * Field secret
     */
    private String secret;

    /**
     * Field active
     */
    private boolean active;

    /**
     * Field ldapGroup
     */
    private LDAPGroup ldapGroup;

    /**
     * Field webhookTriggers
     */
    @ElementCollection(targetClass = WebhookTrigger.class)
    private List<WebhookTrigger> webhookTriggers;

    /**
     * Constructor Webhook creates a new Webhook instance.
     */
    public Webhook() {
        this.key = UUID.randomUUID().toString();
    }
}
