package ch.wisv.events.dashboard.controller;

import ch.wisv.events.core.exception.InvalidVendorException;
import ch.wisv.events.core.exception.VendorNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.vendor.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

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
@RequestMapping("/dashboard/vendors")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardVendorController {


    /**
     * Field vendorService.
     */
    private final VendorService vendorService;

    /**
     * Field eventService.
     */
    private final EventService eventService;

    /**
     * Default constructor.
     *
     * @param vendorService VendorService
     * @param eventService  EventService
     */
    @Autowired
    public DashboardVendorController(VendorService vendorService, EventService eventService) {
        this.vendorService = vendorService;
        this.eventService = eventService;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     * @return path to Thymeleaf template location
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("vendors", vendorService.getAll());

        return "dashboard/vendors/index";
    }

    /**
     * Create a new vendor [GET "/create/"].
     *
     * @param model Spring model
     * @return path to Thymeleaf template location
     */
    @GetMapping("/create/")
    public String create(Model model) {
        if (!model.containsAttribute("vendor")) {
            model.addAttribute("vendor", new Vendor());
        }
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());

        return "dashboard/vendors/create";
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
        try {
            Vendor vendor = vendorService.getByKey(key);
            Collection<Event> eventList = eventService.getUpcomingEvents();
            eventList.addAll(vendor.getEvents());

            model.addAttribute("vendor", vendor);
            model.addAttribute("upcomingEvents", eventList.stream().distinct().collect(
                    Collectors.toCollection(ArrayList::new)));

            return "dashboard/vendors/edit";
        } catch (VendorNotFoundException e) {

            return "redirect:/dashboard/vendors/";
        }
    }

    /**
     * Add a new vendor.
     *
     * @param redirect RedirectAttributes
     * @param model    RequestModel with the needed information
     * @return redirect to other page
     */
    @PostMapping("/add")
    public String add(RedirectAttributes redirect, @ModelAttribute Vendor model) {
        try {
            vendorService.create(model);
            redirect.addFlashAttribute("message", "Vendor has been added!");

            return "redirect:/dashboard/vendors/";
        } catch (InvalidVendorException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("vendor", model);

            return "redirect:/dashboard/vendors/create/";
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
        try {
            vendorService.update(model);
            redirect.addFlashAttribute("message", "Vendor changes saves!");
        } catch (InvalidVendorException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard/vendors/edit/" + model.getKey() + "/";
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
        try {
            Vendor vendor = vendorService.getByKey(key);
            vendorService.delete(vendor);

            redirect.addFlashAttribute("message", "Vendor access for " + vendor.getLdapGroup() + " has been deleted!");

            return "redirect:/dashboard/vendors/";
        } catch (VendorNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/dashboard/vendors/";
        }
    }

}
