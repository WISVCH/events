package ch.wisv.events.sales.controller;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.sales.service.SalesAppProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
@PreAuthorize("hasRole('USER')")
@RequestMapping("/sales")
public class SalesAppMainController {

    /**
     * Field salesAppEventService
     */
    private final SalesAppProductService salesAppEventService;

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param salesAppEventService of type SalesAppProductService
     * @param orderService         of type OrderService
     */
    public SalesAppMainController(SalesAppProductService salesAppEventService, OrderService orderService) {
        this.salesAppEventService = salesAppEventService;
        this.orderService = orderService;
    }

    /**
     * Method index shows the index and check if the user has granted products.
     *
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", salesAppEventService.getAllGrantedProducts());
        model.addAttribute("orderProductDTO", new OrderProductDTO());

        return "sales/index";
    }

    /**
     * Method createOrder ...
     *
     * @param redirect        of type RedirectAttributes
     * @param orderProductDTO of type OrderProductDTO
     * @return String
     */
    @PostMapping("/")
    public String createOrder(RedirectAttributes redirect, @ModelAttribute OrderProductDTO orderProductDTO) {
        if (orderProductDTO.getProducts().isEmpty()) {
            redirect.addFlashAttribute("error", "Shopping cart can not be empty!");

            return "redirect:/sales/";
        }

        try {
            Order order = orderService.createOrderByOrderProductDTO(orderProductDTO);
            order.setCreatedBy("test"); // TODO: change to creators name
            orderService.create(order);

            return "redirect:/sales/order/" + order.getPublicReference() + "/customer/rfid/";
        } catch (OrderInvalidException | ProductNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }
}
