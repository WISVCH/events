package ch.wisv.events.core.service.webhook;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.WebhookTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Service
@Slf4j
public class WebhookTaskServiceImpl implements WebhookTaskService {

    /**
     * Field webhookTaskRepository
     */
    private final WebhookTaskRepository webhookTaskRepository;

    /**
     * Constructor WebhookTaskServiceImpl creates a new WebhookTaskServiceImpl instance.
     *
     * @param webhookTaskRepository of type WebhookTaskRepository
     */
    public WebhookTaskServiceImpl(WebhookTaskRepository webhookTaskRepository) {
        this.webhookTaskRepository = webhookTaskRepository;
    }

    /**
     * Method getAll returns the all of this WebhookTaskService object.
     *
     * @return the all (type List<WebhookTask>) of this WebhookTaskService object.
     */
    @Override
    public List<WebhookTask> getAll() {
        return webhookTaskRepository.findAllByOrderByCreatedAtAsc();
    }

    /**
     * Method create WebhookTask
     *
     * @param webhookTask of type WebhookTask
     */
    @Override
    public void create(WebhookTask webhookTask) {
        webhookTaskRepository.saveAndFlush(webhookTask);
        log.info("Created WebhookTask #" + webhookTask.getId() + ": " + webhookTask.toString());
    }

    /**
     * Method create ...
     *
     * @param webhookTrigger of type WebhookTrigger
     * @param jsonObject     of type Object
     */
    @Override
    public void create(WebhookTrigger webhookTrigger, Webhook webhook, JSONObject jsonObject) {
        WebhookTask webhookTask = new WebhookTask();
        webhookTask.setTrigger(webhookTrigger);
        webhookTask.setWebhook(webhook);
        webhookTask.setObject(jsonObject);

        this.create(webhookTask);
    }
}
