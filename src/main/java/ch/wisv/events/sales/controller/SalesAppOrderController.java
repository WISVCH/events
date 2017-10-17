package ch.wisv.events.sales.controller;

import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.order.Order;
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
@PreAuthorize("hasRole('USER')")
@RequestMapping("/sales/order/{publicReference}")
public class SalesAppOrderController {

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param orderService of type OrderService.
     */
    public SalesAppOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Method overview ...
     *
     * @param redirect of type RedirectAttributes
     * @return String
     */
    @GetMapping("/")
    public String overview(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            Order order = this.orderService.getByReference(publicReference);
            model.addAttribute("order", order);

            return "sales/order/index";
        } catch (EventsModelNotFound e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }

    /**
     * Method complete ...
     *
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     * @return String
     */
    @GetMapping("/{ending}/")
    public String complete(RedirectAttributes redirect, @PathVariable String publicReference,
            @PathVariable String ending
    ) {
        try {
            this.orderService.getByReference(publicReference);

            return "sales/order/" + ending;
        } catch (EventsModelNotFound e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }
}
