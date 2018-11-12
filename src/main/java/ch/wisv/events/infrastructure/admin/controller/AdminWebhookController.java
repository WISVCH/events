package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.domain.model.webhook.Webhook;
import ch.wisv.events.services.WebhookService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * AdminWebhookController.
 */
@Controller
@RequestMapping("/administrator/webhooks")
public class AdminWebhookController extends AbstractAdminController<Webhook> {

    /**
     * AdminWebhookController constructor.
     *
     * @param webhookService of type WebhookService
     */
    @Autowired
    public AdminWebhookController(WebhookService webhookService) {
        super(webhookService, new Webhook(), "webhooks", "webhook");
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
    Webhook saveFile(Webhook model, MultipartFile file) {
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
