package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.domain.model.webhook.WebhookTask;
import ch.wisv.events.services.WebhookTaskService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * AdminWebhookTaskController.
 */
@Controller
@RequestMapping("/administrator/tasks")
public class AdminWebhookTaskController extends AbstractAdminController<WebhookTask> {

    /**
     * AdminWebhookTaskController constructor.
     *
     * @param webhookTaskService of type WebhookTaskService
     */
    @Autowired
    public AdminWebhookTaskController(WebhookTaskService webhookTaskService) {
        super(webhookTaskService, new WebhookTask(), "tasks", "task");
    }

    /**
     * Save a file.
     *
     * @param model of type AbstractModel
     * @param file  of type MultipartFile
     *
     * @return T
     */
    @Override
    WebhookTask saveFile(WebhookTask model, MultipartFile file) {
        return model;
    }

    /**
     * Add Model to the index page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeIndex() {
        return null;
    }

    /**
     * Add Model to the view page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeView() {
        return null;
    }

    /**
     * Add Model to the edit page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeCreateEdit() {
        return null;
    }
}
