package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import org.thymeleaf.util.ArrayUtils;

/**
 * WebshopController class.
 */
abstract class WebshopController {

    /** OrderService. */
    protected final OrderService orderService;

    /**
     * WebshopController constructor.
     *
     * @param orderService of type OrderService
     */
    protected WebshopController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Assert if an order is suitable for checkout.
     *
     * @param order of type Order
     *
     * @throws OrderInvalidException when Order is invalid
     */
    void assertOrderIsSuitableForCheckout(Order order) throws OrderInvalidException {
        OrderStatus[] stopOrderStatus = new OrderStatus[]{OrderStatus.EXPIRED, OrderStatus.PAID, OrderStatus.RESERVATION, OrderStatus.REJECTED};

        if (ArrayUtils.contains(stopOrderStatus, order.getStatus())) {
            throw new OrderInvalidException("Order is in a invalid state");
        }
    }
}
