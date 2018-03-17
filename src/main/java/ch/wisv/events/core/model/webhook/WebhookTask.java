package ch.wisv.events.core.model.webhook;

import lombok.Data;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.time.LocalDateTime;

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
public class WebhookTask {

    /**
     * ID of the product, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Field trigger
     */
    private WebhookTrigger trigger;

    /**
     * Field webhook
     */
    @ManyToOne
    private Webhook webhook;

    /**
     * Field object
     */
    private JSONObject object;

    /**
     * Field createdAt
     */
    private LocalDateTime createdAt;

    /**
     * Field webhookTaskStatus
     */
    private WebhookTaskStatus webhookTaskStatus;

    /**
     * Field webhookError
     */
    @Column(columnDefinition = "TEXT")
    private String webhookError;

    /**
     * Constructor WebhookTask creates a new WebhookTask instance.
     */
    public WebhookTask() {
        this.createdAt = LocalDateTime.now();
        this.webhookTaskStatus = WebhookTaskStatus.PENDING;
    }

    /**
     * Method toString ...
     *
     * @return String
     */
    @Override
    public String toString() {
        return "WebhookTask{" + "trigger=" + trigger + ", webhook=" + webhook.getPayloadUrl() + ", object=" + object.toString() + '}';
    }
}
