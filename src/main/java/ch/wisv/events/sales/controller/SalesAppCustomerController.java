package ch.wisv.events.sales.controller;

import ch.wisv.events.core.exception.CustomerNotFound;
import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.sales.service.SalesAppOrderService;
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

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Field customerService
     */
    private final CustomerService customerService;

    /**
     * Field salesAppOrderService
     */
    private final SalesAppOrderService salesAppOrderService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param orderService         of type OrderService
     * @param customerService      of type CustomerService
     * @param salesAppOrderService of type SalesAppOrderService
     */
    public SalesAppCustomerController(OrderService orderService, CustomerService customerService, SalesAppOrderService salesAppOrderService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.salesAppOrderService = salesAppOrderService;
    }


    /**
     * Method customer ...
     *
     * @param model of type Model
     * @return String
     */
    @GetMapping("/rfid/")
    public String customer(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            Order order = this.orderService.getByReference(publicReference);
            model.addAttribute("order", order);

            return "sales/order/scan";
        } catch (EventsModelNotFound e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }

    /**
     * Method customer ...
     *
     * @param order of type Model
     * @return String
     */
    @PostMapping("/rfid/")
    public String addCustomerToOrder(RedirectAttributes redirect, Order order) {
        try {
            this.salesAppOrderService.addCustomerToOrder(order, order.getCustomer());
        } catch (EventsModelNotFound e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        } catch (CustomerNotFound e) {
            return "redirect:/sales/order/customer/create/";
        }

        return "redirect:/sales/order/overview/" + order.getPublicReference() + "/";
    }

    /**
     * Method index shows the index and check if the user has granted products.
     *
     * @return String
     */
    @GetMapping("/create/")
    public String create(Model model) {
        if (!model.containsAttribute("order")) {
            return "redirect:/sales/";
        }

        return "sales/customer/create";
    }

    /**
     * Method createOrder ...
     *
     * @param order of type Order
     * @return String
     */
    @PostMapping("/create/")
    public String create(@ModelAttribute Order order) {
        this.customerService.create(order.getCustomer());
        this.salesAppOrderService.addCustomerToOrder(order, order.getCustomer());

        return "redirect:/sales/order/overview/" + order.getPublicReference() + "/";
    }
}
