package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@RequestMapping(value = "/sales/sell/payment/{publicReference}")
public class SalesSellPaymentController {

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param orderService of type OrderService.
     */
    @Autowired
    public SalesSellPaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * /sales/payment/order/{publicReference}
     *
     * @return String
     */
    @PostMapping
    public String payment(
            @PathVariable String publicReference,
            @RequestParam(value = "method") PaymentMethod method
    ) {
        try {
            Order order = orderService.getByReference(publicReference);
            order.setPaymentMethod((order.getAmount()) == 0.d ? PaymentMethod.OTHER : method);

            orderService.create(order);
            orderService.updateOrderStatusPaid(order);

            return "redirect:/sales/sell/order/" + order.getPublicReference() + "/complete";
        } catch (EventsException e) {
            return "redirect:/sales/sell/";
        }
    }
}
