package ch.wisv.events.admin.controller;

import ch.wisv.events.core.service.webhook.WebhookTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
@Controller
@RequestMapping("/administrator/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardTasksController {

    /**
     * Field webhookService
     */
    private final WebhookTaskService webhookTaskService;

    /**
     * Default constructor.
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
     * @return path to Thymeleaf template location
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", webhookTaskService.getAll());

        return "admin/tasks/index";
    }
}
