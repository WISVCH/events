package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(value = "/administrator/orders")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardOrderController {

    /**
     * OrderService.
     */
    private final OrderService orderService;
    private final String REDIRECT_ORDERS_OVERVIEW = "redirect:/administrator/orders/";

    /**
     * Default constructor
     *
     * @param orderService OrderService
     */
    public DashboardOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Show list of all orders.
     *
     * @param model of type Model
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("orders", this.orderService.getAllOrders());

        return "admin/orders/index";
    }

    /**
     * Get a view of an order.
     *
     * @param model of type Model
     * @param key   of type String
     * @return String
     */
    @GetMapping("/view/{key}/")
    public String edit(Model model, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("order", order);

            return "admin/orders/view";
        } catch (OrderNotFoundException e) {
            return REDIRECT_ORDERS_OVERVIEW;
        }
    }

    /**
     * Method delete ...
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/delete/{key}")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            orderService.updateOrderStatus(order, OrderStatus.REJECTED);

            redirect.addFlashAttribute("message", "Order #" + order.getId() + " has been rejected!");
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return REDIRECT_ORDERS_OVERVIEW;
    }
}
