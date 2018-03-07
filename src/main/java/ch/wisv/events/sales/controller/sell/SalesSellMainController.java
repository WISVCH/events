package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.sales.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/sales/sell")
@PreAuthorize("hasRole('USER')")
public class SalesSellMainController {

    private final AuthenticationService authenticationService;

    private final SalesService salesService;

    private final OrderService orderService;

    @Autowired
    public SalesSellMainController(
            AuthenticationService authenticationService,
            SalesService salesService,
            OrderService orderService
    ) {
        this.authenticationService = authenticationService;
        this.salesService = salesService;
        this.orderService = orderService;
    }

    @GetMapping
    public String index(Model model) {
        Customer currentUser = authenticationService.getCurrentCustomer();
        model.addAttribute("products", salesService.getAllGrantedProductByCustomer(currentUser));

        return "sales/sell/index";
    }

    @PostMapping
    public String createOrder(RedirectAttributes redirect, @ModelAttribute OrderProductDTO orderProductDTO) {
        if (orderProductDTO.getProducts().isEmpty()) {
            redirect.addFlashAttribute("error", "Shopping cart can not be empty!");

            return "redirect:/sales/sell/";
        }

        try {
            Order order = orderService.createOrderByOrderProductDTO(orderProductDTO);
            order.setCreatedBy(authenticationService.getCurrentCustomer().getName());
            order.setStatus(OrderStatus.TEMP);
            orderService.tempSaveOrder(order);

            return "redirect:/sales/sell/customer/" + order.getPublicReference();
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }
}
