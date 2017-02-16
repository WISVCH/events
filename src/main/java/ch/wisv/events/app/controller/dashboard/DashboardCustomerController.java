package ch.wisv.events.app.controller.dashboard;

import ch.wisv.events.core.exception.CustomerNotFound;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.product.SoldProductService;
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

    /**
     * CustomerService.
     */
    private final CustomerService customerService;


    private final SoldProductService soldProductService;

    /**
     * Autowired constructor.
     *
     * @param customerService CustomerService
     * @param soldProductService
     */
    @Autowired
    public DashboardCustomerController(CustomerService customerService, SoldProductService soldProductService) {
        this.customerService = customerService;
        this.soldProductService = soldProductService;
    }


    /**
     * Index of customers pages.
     *
     * @param model String model
     * @return path to customers index template
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());

        return "dashboard/customers/index";
    }

    /**
     * Create a new customer page.
     *
     * @param model String model
     * @return path to customer create template
     */
    @GetMapping("/create/")
    public String create(Model model) {
        if (!model.containsAttribute("customer")) {
            model.addAttribute("customer", new Customer());
        }

        return "dashboard/customers/create";
    }

    /**
     * Edit an existing customer page. It will redirect the user when the key provided does not belong to any of the
     * customers available.
     *
     * @param model String model
     * @param key   key of the customer some want to edit
     * @return path to the customer edit template
     */
    @GetMapping("/edit/{key}/")
    public String edit(Model model, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            model.addAttribute("customer", customer);
            model.addAttribute("products", soldProductService.getByCustomer(customer));

            return "dashboard/customers/edit";
        } catch (CustomerNotFound e) {
            return "redirect:/dashboard/customers/";
        }
    }

    /**
     * Creates a new customer using the Customer model. It will redirect the user back to the create page when not
     * all the required field are filled in.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param model    Customer model
     * @return redirect
     */
    @PostMapping("/add")
    public String add(RedirectAttributes redirect, @ModelAttribute Customer model) {
        try {
            customerService.create(model);
            redirect.addFlashAttribute("message", "Customer with name " + model.getName() + "  had been added!");

            return "redirect:/dashboard/customers/";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("customer", model);

            return "redirect:/dashboard/customers/create/";
        }

    }

    /**
     * Updates an existing customer using the Customer model. It will show an error when not all the required fields
     * are filled in.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param model    Customer model
     * @return redirect
     */
    @PostMapping("/update")
    public String update(RedirectAttributes redirect, @ModelAttribute Customer model) {
        try {
            customerService.update(model);
            redirect.addFlashAttribute("message", "Customer changes have been saved!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/customers/edit/" + model.getKey() + "/";
    }

    /**
     * Deletes a existing customer. It will show an error when the customer that would be deleted already ordered
     * some products. Because in this case it is not possible to only delete the customer, without also deleting the
     * orders.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param key      Vendor model
     * @return redirect
     */
    @GetMapping("/delete/{key}")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            customerService.delete(customer);

            redirect.addFlashAttribute("message", "Customer with name " + customer.getName() + " has been deleted!");

            return "redirect:/dashboard/customers/";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/dashboard/customers/";
        }
    }

}
