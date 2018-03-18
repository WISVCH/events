package ch.wisv.events.admin.controller;

import ch.wisv.events.core.service.webhook.WebhookTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardTasksController {

    /** WebhookTaskService. */
    private final WebhookTaskService webhookTaskService;

    /**
     * DashboardTasksController constructor.
     *
     * @param webhookTaskService of type WebhookTaskService.
     */
    @Autowired
    public DashboardTasksController(WebhookTaskService webhookTaskService) {
        this.webhookTaskService = webhookTaskService;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute("tasks", webhookTaskService.getAll());

        return "admin/tasks/index";
    }
}
