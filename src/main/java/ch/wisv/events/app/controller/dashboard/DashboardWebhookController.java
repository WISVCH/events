package ch.wisv.events.app.controller.dashboard;

import ch.wisv.events.core.exception.InvalidWebhookException;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.service.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
@RequestMapping("/dashboard/webhooks")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardWebhookController {

    /**
     * Field webhookService
     */
    private final WebhookService webhookService;

    /**
     * Default constructor.
     *
     * @param webhookService of type WebhookService.
     */
    @Autowired
    public DashboardWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     * @return path to Thymeleaf template location
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("webhooks", webhookService.getAll());

        return "dashboard/webhooks/index";
    }

    /**
     * Create a new vendor [GET "/create/"].
     *
     * @param model Spring model
     * @return path to Thymeleaf template location
     */
    @GetMapping("/create/")
    public String create(Model model) {
        if (!model.containsAttribute("webhook")) {
            model.addAttribute("webhook", new Webhook());
        }

        return "dashboard/webhooks/create";
    }

    /**
     * Edit existing vendor [GET "/edit/{key}"].
     *
     * @param model Spring model
     * @param key   key of the vendor
     * @return path to Thymeleaf template location
     */
    @GetMapping("/edit/{key}/")
    public String edit(Model model, @PathVariable String key) {
//        try {
//            Vendor vendor = vendorService.getByKey(key);
//            Collection<Event> eventList = eventService.getUpcomingEvents();
//            eventList.addAll(vendor.getEvents());
//
//            model.addAttribute("vendor", vendor);
//            model.addAttribute("upcomingEvents", eventList.stream().distinct().collect(
//                    Collectors.toCollection(ArrayList::new)));
//
//            return "dashboard/webhooks/edit";
//        } catch (VendorNotFoundException e) {
//
//            return "redirect:/dashboard/webhooks/";
//        }
        return "";
    }

    /**
     * Add a new vendor.
     *
     * @param redirect RedirectAttributes
     * @param model    RequestModel with the needed information
     * @return redirect to other page
     */
    @PostMapping("/add")
    public String add(RedirectAttributes redirect, @ModelAttribute Webhook model) {
        try {
            webhookService.create(model);
            redirect.addFlashAttribute("message", "Webhook has been added!");

            return "redirect:/dashboard/webhooks/";
        } catch (InvalidWebhookException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("webhook", model);

            return "redirect:/dashboard/webhooks/create/";
        }
    }

    /**
     * Update a existing vendor.
     *
     * @param redirect RedirectAttributes
     * @param model    Vendor model with the needed information
     * @return redirect to edit page
     */
    @PostMapping("/update")
    public String update(RedirectAttributes redirect, @ModelAttribute Vendor model) {
//        try {
//            webhookService.update(model);
//            redirect.addFlashAttribute("message", "Vendor changes saves!");
//        } catch (InvalidVendorException e) {
//            redirect.addFlashAttribute("error", e.getMessage());
//        }
//
//        return "redirect:/dashboard/webhooks/edit/" + model.getKey() + "/";
        return "";
    }

    /**
     * Update a existing vendor.
     *
     * @param redirect RedirectAttributes
     * @param key      Vendor model with the needed information
     * @return redirect to edit page
     */
    @GetMapping("/delete/{key}")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
//        try {
//            Vendor vendor = vendorService.getByKey(key);
//            vendorService.delete(vendor);
//
//            redirect.addFlashAttribute("message", "Vendor access for " + vendor.getLdapGroup() + " has been deleted!");
//
//            return "redirect:/dashboard/webhooks/";
//        } catch (VendorNotFoundException e) {
//            redirect.addFlashAttribute("error", e.getMessage());
//
//            return "redirect:/dashboard/webhooks/";
//        }
        return "";
    }

}
