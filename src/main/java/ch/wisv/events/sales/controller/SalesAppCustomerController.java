package ch.wisv.events.sales.controller;

import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
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
@PreAuthorize("hasRole('USER')")
@RequestMapping("/sales/order/{publicReference}/customer")
public class SalesAppCustomerController {

    private final String REDIRECT_SALES_HOME = "redirect:/sales/";
    private final String REDIRECT_CUSTOMER_CREATE = "redirect:/sales/order/%s/customer/create/";
    private final String REDIRECT_ORDER_OVERVIEW = "redirect:/sales/order/%s/";

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Field customerService
     */
    private final CustomerService customerService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param orderService    of type OrderService
     * @param customerService of type CustomerService
     */
    public SalesAppCustomerController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    /**
     * GetMapping "/sales/order/{publicReference}/customer/rfid/" show the view to scan a rfid card.
     *
     * @param model of type Model
     * @return String
     */
    @GetMapping("/rfid/")
    public String customer(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            orderService.getByReference(publicReference);
            model.addAttribute("customer", new Customer());

            return "sales/customer/rfid";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_SALES_HOME;
        }
    }

    /**
     * PostMapping "/sales/order/{publicReference}/customer/rfid/".
     *
     * @param customer        of type Customer
     * @param publicReference of type String
     * @return String
     */
    @PostMapping("/rfid/")
    public String addCustomerToOrder(RedirectAttributes redirect, @ModelAttribute Customer customer, @PathVariable String publicReference) {
        try {
            Order order = orderService.getByReference(publicReference);
            if (customer.getEmail() != null && !customer.getEmail().equals("")) {
                customer = customerService.getByEmail(customer.getEmail());
            } else {
                customer = customerService.getByRfidToken(customer.getRfidToken());
            }

            order.setCustomer(customer);
            orderService.assertIsValidForCustomer(order);
            orderService.update(order);

            return String.format(REDIRECT_ORDER_OVERVIEW, order.getPublicReference());
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_SALES_HOME;
        } catch (CustomerNotFoundException e) {
            redirect.addFlashAttribute("customer", customer);

            return String.format(REDIRECT_CUSTOMER_CREATE, publicReference);
        }
    }

    /**
     * GetMapping "/sales/order/{publicReference}/customer/create/" shows view of customer create.
     *
     * @return String
     */
    @GetMapping("/create/")
    public String create(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            orderService.getByReference(publicReference);
            if (!model.containsAttribute("customer")) {
                model.addAttribute("customer", new Customer());
            }

            return "sales/customer/create";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_SALES_HOME;
        }
    }

    /**
     * PostMapping "/sales/order/{publicReference}/customer/create/" creates a new customer.
     *
     * @param customer        of type Customer
     * @param publicReference of type String
     * @return String
     */
    @PostMapping("/create/")
    public String create(RedirectAttributes redirect, @ModelAttribute Customer customer, @PathVariable String publicReference) {
        try {
            Order order = orderService.getByReference(publicReference);
            customerService.create(customer);
            order.setCustomer(customer);
            orderService.update(order);

            redirect.addFlashAttribute("success", "Customer successfully created!");

            return String.format(REDIRECT_ORDER_OVERVIEW, order.getPublicReference());
        } catch (CustomerInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("customer", customer);

            return String.format(REDIRECT_CUSTOMER_CREATE, publicReference);
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_SALES_HOME;
        }
    }
}
