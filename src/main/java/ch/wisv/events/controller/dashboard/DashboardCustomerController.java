package ch.wisv.events.controller.dashboard;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.exception.CustomerNotFound;
import ch.wisv.events.exception.InvalidCustomerException;
import ch.wisv.events.service.order.CustomerService;
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
@RequestMapping(value = "/dashboard/customers")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardCustomerController {

    private final CustomerService customerService;

    @Autowired
    public DashboardCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());

        return "dashboard/customers/index";
    }

    @GetMapping("/create/")
    public String create(Model model) {
        if (!model.containsAttribute("customer")) model.addAttribute("customer", new Customer());

        return "dashboard/customers/create";
    }

    @GetMapping("/edit/{key}/")
    public String edit(Model model, @PathVariable String key) {
        try {
            Customer customer = customerService.getCustomerByKey(key);
            model.addAttribute("customer", customer);

            return "dashboard/customers/edit";
        } catch (CustomerNotFound e) {
            return "redirect:/dashboard/customers/";
        }
    }

    @PostMapping("/add")
    public String add(RedirectAttributes redirect, @ModelAttribute Customer model) {
        try {
            customerService.addCustomer(model);
            redirect.addFlashAttribute("message", "Customer with name " + model.getName() +  "  had been added!");

            return "redirect:/dashboard/customers/";
        } catch (InvalidCustomerException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("customer", model);

            return "redirect:/dashboard/customers/create/";
        }

    }

    @PostMapping("/update")
    public String update(RedirectAttributes redirect, @ModelAttribute Customer model) {
        try {
            customerService.updateCustomer(model);
            redirect.addFlashAttribute("message", "Customer changes have been saved!");
        } catch (InvalidCustomerException e) {
            redirect.addFlashAttribute("error", e.getMessage());

        }
        return "redirect:/dashboard/customers/edit/" + model.getKey() + "/";
    }

    /**
     * Update a existing vendor
     *
     * @param redirect RedirectAttributes
     * @param key    Vendor model with the needed information
     * @return redirect to edit page
     */
    @GetMapping("/delete/{key}")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Customer customer = customerService.getCustomerByKey(key);
            customerService.deleteVendor(customer);

            redirect.addFlashAttribute("message", "Customer with name " + customer.getName() + " has been deleted!");

            return "redirect:/dashboard/customers/";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/dashboard/customers/";
        }
    }

}
