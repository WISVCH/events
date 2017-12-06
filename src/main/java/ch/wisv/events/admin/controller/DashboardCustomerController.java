package ch.wisv.events.admin.controller;

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
@RequestMapping(value = "/administrator/customers")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardCustomerController {

    /**
     * CustomerService.
     */
    private final CustomerService customerService;

    /**
     * SoldProductService
     */
    private final SoldProductService soldProductService;

    /**
     * Autowired constructor.
     *
     * @param customerService    CustomerService
     * @param soldProductService SoldProductService
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

        return "admin/customers/index";
    }

    /**
     * Edit an existing customer page. It will redirect the user when the key provided does not belong to any of the
     * customers available.
     *
     * @param model String model
     * @param key   key of the customer some want to edit
     * @return path to the customer edit template
     */
    @GetMapping("/view/{key}/")
    public String view(Model model, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            model.addAttribute("customer", customer);
            model.addAttribute("soldProducts", soldProductService.getByCustomer(customer));

            return "admin/customers/view";
        } catch (CustomerNotFound e) {
            return "redirect:/administrator/customers/";
        }
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

        return "admin/customers/customer";
    }

    /**
     * Creates a new customer using the Customer model. It will redirect the user back to the create page when not
     * all the required field are filled in.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param model    Customer model
     * @return redirect
     */
    @PostMapping("/create/")
    public String create(RedirectAttributes redirect, @ModelAttribute Customer model) {
        try {
            customerService.create(model);
            redirect.addFlashAttribute("success", "Customer with name " + model.getName() + "  has been created!");

            return "redirect:/administrator/customers/";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("customer", model);

            return "redirect:/administrator/customers/create/";
        }

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
            if (!model.containsAttribute("customer")) {
                model.addAttribute("customer", customer);
            }
            model.addAttribute("products", soldProductService.getByCustomer(customer));

            return "admin/customers/customer";
        } catch (CustomerNotFound e) {
            return "redirect:/administrator/customers/";
        }
    }

    /**
     * Updates an existing customer using the Customer customer. It will show an error when not all the required fields
     * are filled in.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param customer Customer customer
     * @return redirect
     */
    @PostMapping("/edit/{key}/")
    public String edit(RedirectAttributes redirect, @ModelAttribute Customer customer) {
        try {
            customerService.update(customer);
            redirect.addFlashAttribute("success", "Customer changes have been saved!");

            return "redirect:/administrator/customers/view/" + customer.getKey() + "/";
        } catch (Exception e) {
            redirect.addFlashAttribute("customer", customer);
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/administrator/customers/edit/" + customer.getKey() + "/";
        }
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
    @GetMapping("/delete/{key}/")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            customerService.delete(customer);

            redirect.addFlashAttribute("message", "Customer with name " + customer.getName() + " has been deleted!");

            return "redirect:/administrator/customers/";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/administrator/customers/";
        }
    }
}
