package ch.wisv.events.tickets.controller;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import org.thymeleaf.util.ArrayUtils;

abstract public class WebshopController {

    protected final OrderService orderService;

    protected WebshopController(OrderService orderService) {
        this.orderService = orderService;
    }

    protected void assertShouldContinue(Order order) throws OrderInvalidException {
        OrderStatus[] stopOrderStatus = new OrderStatus[]{OrderStatus.EXPIRED, OrderStatus.PAID, OrderStatus.RESERVATION, OrderStatus.REJECTED,};

        if (ArrayUtils.contains(stopOrderStatus, order.getStatus())) {
            throw new OrderInvalidException("Order is in a invalid state");
        }
    }
}
