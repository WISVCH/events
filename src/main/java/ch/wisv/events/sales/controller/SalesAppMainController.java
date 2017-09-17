package ch.wisv.events.sales.controller;

import ch.wisv.events.core.exception.EventsSalesAppException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.sales.service.SalesAppProductService;
import ch.wisv.events.sales.service.SalesAppOrderService;
import ch.wisv.events.sales.service.SalesAppSoldProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

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
     * Field salesAppSoldProductService
     */
    private final SalesAppSoldProductService salesAppSoldProductService;

    /**
     * Field salesAppOrderService
     */
    private final SalesAppOrderService salesAppOrderService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param salesAppEventService       of type SalesAppProductService.
     * @param salesAppSoldProductService of type SalesAppSoldProductService.
     * @param salesAppOrderService               of type OrderService.
     */
    public SalesAppMainController(SalesAppProductService salesAppEventService, SalesAppSoldProductService salesAppSoldProductService, SalesAppOrderService salesAppOrderService) {
        this.salesAppEventService = salesAppEventService;
        this.salesAppSoldProductService = salesAppSoldProductService;
        this.salesAppOrderService = salesAppOrderService;
    }

    /**
     * Method index shows the index and check if the user has granted products.
     *
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", this.salesAppEventService.getAllGrantedProducts());
        model.addAttribute("order", new Order());

        return "sales/index";
    }

    /**
     * Method createOrder ...
     *
     * @param redirect of type RedirectAttributes
     * @param order    of type Order
     * @return String
     */
    @PostMapping("/")
    public String createOrder(RedirectAttributes redirect, @ModelAttribute Order order) {
        try {
            Map<Product, Long> productCount = order.getProducts().stream()
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
            productCount.forEach(this.salesAppSoldProductService::assertAmountOfProductLeft);
            this.salesAppOrderService.create(order);

            redirect.addFlashAttribute("order", order);

            return "redirect:/sales/order/" + order.getPublicReference() + "/customer/rfid/";
        } catch (EventsSalesAppException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }
}
