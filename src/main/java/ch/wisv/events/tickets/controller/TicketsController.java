package ch.wisv.events.tickets.controller;

import ch.wisv.events.core.exception.EventsInvalidException;
import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.exception.ProductNotFound;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.tickets.service.TicketsService;
import org.springframework.security.access.AccessDeniedException;
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
@PreAuthorize("isAuthenticated()")
public class TicketsController {

    /**
     * Field productService
     */
    private final ProductService productService;

    /**
     * Field ticketsService
     */
    private final TicketsService ticketsService;

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Constructor TicketsController.
     *
     * @param productService of type ProductService
     * @param ticketService  of type TicketsService
     * @param orderService   of type OrderService
     */
    public TicketsController(ProductService productService, TicketsService ticketService, OrderService orderService) {
        this.productService = productService;
        this.ticketsService = ticketService;
        this.orderService = orderService;
    }

    /**
     * GetMapping for "/".
     *
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.getAvailableProducts());
        model.addAttribute("customer", ticketsService.getCurrentCustomer());
        model.addAttribute("orderProduct", new OrderProductDTO());

        return "tickets/index";
    }

    /**
     * GetMapping for "/checkout/{key}/".
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/checkout/{key}/")
    public String checkout(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            if (ticketsService.getCurrentCustomer().equals(order.getCustomer())) {
                model.addAttribute("order", order);

                return "tickets/checkout";
            } else {
                throw new AccessDeniedException("Access denied!");
            }
        } catch (EventsModelNotFound | AccessDeniedException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * GetMapping for "/cancel/{key}".
     *
     * @param key of type String
     * @return String
     */
    @GetMapping("/cancel/{key}/")
    public String cancel(@PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);

            if (ticketsService.getCurrentCustomer().equals(order.getCustomer())) {
                orderService.updateOrderStatus(order, OrderStatus.CANCELLED);
            }
        } catch (EventsModelNotFound ignored) {
        }

        return "redirect:/";
    }

    /**
     * GetMapping for "/payments/{key}".
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/payment/{key}/")
    public String payment(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);

            if (ticketsService.getCurrentCustomer().equals(order.getCustomer())) {
                return "redirect:" + ticketsService.getPaymentsMollieUrl(order);
            } else {
                throw new AccessDeniedException("Access denied!");
            }
        } catch (EventsModelNotFound | EventsInvalidException | AccessDeniedException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/checkout/" + key + "/";
    }

    /**
     * GetMapping for "/complete/{key}/". Redirect from mollie payments.
     *
     * @param model             of type Model
     * @param redirect          of type RedirectAttributes
     * @param key               of type String
     * @param paymentsReference of type String
     * @return String
     */
    @GetMapping("/complete/{key}/")
    public String complete(Model model, RedirectAttributes redirect, @PathVariable String key, @RequestParam("reference") String paymentsReference) {
        try {
            Order order = orderService.getByReference(key);

            if (ticketsService.getCurrentCustomer().equals(order.getCustomer())) {
                order = ticketsService.updateOrderStatus(order, paymentsReference);
                model.addAttribute("order", order);

                return "tickets/complete";
            }
        } catch (EventsModelNotFound | EventsInvalidException | AccessDeniedException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/checkout/" + key + "/";
    }

    /**
     * Creates an order with Products and current Customer.
     *
     * @param redirect        of type RedirectAttributes
     * @param orderProductDTO of type OrderProductDTO
     * @return String
     */
    @PostMapping("/checkout/")
    public String checkout(RedirectAttributes redirect, @ModelAttribute OrderProductDTO orderProductDTO) {
        try {
            if (orderProductDTO.getProducts().isEmpty()) {
                redirect.addFlashAttribute("error", "Shopping cart can not be empty!");

                return "redirect:/";
            }

            Order order = orderService.createOrderByOrderProductDTO(orderProductDTO);
            order.setCustomer(ticketsService.getCurrentCustomer());
            order.setCreatedBy("events-online");

            orderService.assertIsValidForCustomer(order);
            orderService.create(order);

            return "redirect:/checkout/" + order.getPublicReference() + "/";
        } catch (ProductNotFound | EventsInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }
}