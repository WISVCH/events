package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.*;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
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
@RequestMapping("/checkout")
public class WebshopCheckoutController extends WebshopController {

    private final OrderService orderService;

    private final OrderValidationService orderValidationService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     */
    public WebshopCheckoutController(OrderService orderService, OrderValidationService orderValidationService) {
        super(orderService);
        this.orderService = orderService;
        this.orderValidationService = orderValidationService;
    }

    @PostMapping("")
    public String checkout(RedirectAttributes redirect, @ModelAttribute OrderProductDTO orderProductDTO) {
        try {
            if (orderProductDTO.getProducts().isEmpty()) {
                redirect.addFlashAttribute("error", "Shopping basket can not be empty!");

                return "redirect:/";
            }

            Order order = orderService.createOrderByOrderProductDTO(orderProductDTO);
            order.setCreatedBy("events-webshop");
            orderValidationService.assertOrderIsValid(order);
            orderService.create(order);

            return "redirect:/checkout/" + order.getPublicReference();
        } catch (ProductNotFoundException | EventNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", "Limit exceeded: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * GetMapping for "/checkout/{key}/".
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/{key}")
    public String checkout(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertShouldContinue(order);

            model.addAttribute("order", order);

            return "webshop/checkout/index";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    @GetMapping("/{key}/cancel")
    public String cancelOrder(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertShouldContinue(order);
            orderService.updateOrderStatus(order, OrderStatus.CANCELLED);

            redirect.addFlashAttribute("success", "Order has successfully been cancelled.");

            return "redirect:/";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }
}
