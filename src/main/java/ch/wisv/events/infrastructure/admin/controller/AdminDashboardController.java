package ch.wisv.events.infrastructure.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminWebhookController.
 */
@Controller
@RequestMapping("/administrator")
public class AdminDashboardController {

    /**
     * AdminWebhookController constructor.
     */
    @Autowired
    public AdminDashboardController() {
    }

    /**
     * Index of the log file.
     *
     * @return String
     */
    @GetMapping
    public String index() {
        return "admin/index";
    }
}
