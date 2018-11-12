package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminWebhookController.
 */
@Controller
@RequestMapping("/administrator/logs")
public class AdminLogsController {

    /**
     * LogService.
     */
    private final LogService logService;

    /**
     * AdminWebhookController constructor.
     *
     * @param logService of type LogService
     */
    @Autowired
    public AdminLogsController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Index of the log file.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("logs", logService.loadLoggingFile());

        return "admin/logs/index";
    }
}
