package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.*;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
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
@RequestMapping("/checkout/{key}/customer")
public class WebshopCustomerController extends WebshopController {

    private final CustomerService customerService;

    private final AuthenticationService authenticationService;

    private final OrderValidationService orderValidationService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param customerService        of type CustomerService
     * @param authenticationService  of type AuthenticationService
     * @param orderValidationService of type OrderValidationService
     */
    public WebshopCustomerController(
            OrderService orderService,
            CustomerService customerService,
            AuthenticationService authenticationService,
            OrderValidationService orderValidationService
    ) {
        super(orderService);
        this.customerService = customerService;
        this.authenticationService = authenticationService;
        this.orderValidationService = orderValidationService;
    }

    @GetMapping
    public String customerOptions(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertShouldContinue(order);
            model.addAttribute("order", order);

            return "webshop/checkout/customer";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    @GetMapping("/chconnect")
    @PreAuthorize("hasRole('USER')")
    public String customerChConnect(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertShouldContinue(order);
            Customer customer = authenticationService.getCurrentCustomer();

            this.addCustomerToOrder(order, customer);

            return "redirect:/checkout/" + order.getPublicReference() + "/payment";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    private void addCustomerToOrder(Order order, Customer customer)
            throws OrderInvalidException, OrderExceedCustomerLimitException, OrderNotFoundException {
        orderValidationService.assertOrderIsValidForCustomer(order, customer);

        order.setOwner(customer);
        orderService.update(order);
        orderService.updateOrderStatus(order, OrderStatus.ASSIGNED);
    }

    @GetMapping("/guest")
    public String customerGuest(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertShouldContinue(order);

            if (!model.containsAttribute("customer")) {
                model.addAttribute("customer", new Customer());
            }
            model.addAttribute("order", order);

            return "webshop/checkout/create";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    @PostMapping("/guest")
    public String addNewCustomer(RedirectAttributes redirect, @PathVariable String key, @ModelAttribute Customer customer) {
        try {
            Order order = orderService.getByReference(key);

            Customer existingCustomer = this.getExistingCustomer(customer);

            if (existingCustomer == null) {
                customerService.create(customer);
                this.addCustomerToOrder(order, customer);
            } else {
                this.addCustomerToOrder(order, existingCustomer);
            }

            return "redirect:/checkout/" + order.getPublicReference() + "/payment";
        } catch (CustomerInvalidException e) {
            redirect.addFlashAttribute("customer", customer);

            return "redirect:/checkout/" + key + "/customer";

        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    private Customer getExistingCustomer(Customer customer) {
        try {
            return customerService.getByEmail(customer.getEmail());
        } catch (CustomerNotFoundException e) {
            try {
                return customerService.getByUsername(customer.getChUsername());
            } catch (CustomerNotFoundException ignored) {
            }
        }
        return null;
    }
}
