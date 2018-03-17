package ch.wisv.events.tickets.service;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
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
@Service
public class WebshopServiceImpl implements WebshopService {

    /**
     * PaymentsService paymentsService.
     */
    private final PaymentsService paymentsService;

    /**
     * OrderService orderService.
     */
    private final OrderService orderService;

    /**
     * Constructor.
     *
     * @param paymentsService of type PaymentsService
     * @param orderService    of type OrderService
     */
    public WebshopServiceImpl(PaymentsService paymentsService, OrderService orderService) {
        this.paymentsService = paymentsService;
        this.orderService = orderService;
    }

    @Override
    public List<Event> filterNotSalableProducts(List<Event> events) {
        events.forEach(event -> {
            List<Product> filterSalableProducts = event.getProducts().stream()
                    .filter(this.filterProductBySellInterval())
                    .collect(Collectors.toList());

            event.setProducts(filterSalableProducts);
        });

        return events;
    }

    private Predicate<Product> filterProductBySellInterval() {
        return product ->
                LocalDateTime.now().isAfter(product.getSellStart()) && LocalDateTime.now().isBefore(product.getSellEnd());
    }

    /**
     * Update the status of the Order via the Payments API.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     */
    @Override
    public void updateOrderStatus(Order order, String paymentsReference) throws PaymentsStatusUnknown, OrderInvalidException {
        String status = paymentsService.getPaymentsOrderStatus(paymentsReference);

        switch (status) {
            case "WAITING":
                orderService.updateOrderStatus(order, OrderStatus.PENDING);
                break;
            case "PAID":
                orderService.updateOrderStatus(order, OrderStatus.PAID);
                break;
            case "CANCELLED":
                orderService.updateOrderStatus(order, OrderStatus.CANCELLED);
                break;
            default:
                throw new PaymentsStatusUnknown(status);
        }
    }
}
