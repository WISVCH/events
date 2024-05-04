package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import org.thymeleaf.util.ArrayUtils;

/**
 * WebshopController class.
 */
abstract class WebshopController {

    /** Redirect to events home. */
    static final String REDIRECT_EVENTS_HOME = "redirect:/";

    /** Model attr Customer. */
    static final String MODEL_ATTR_CUSTOMER = "customer";

    /** Model attr Order. */
    static final String MODEL_ATTR_ORDER = "order";

    /** Model attr message type error. */
    static final String MODEL_ATTR_ERROR = "error";

    /** Model attr message type success. */
    static final String MODEL_ATTR_SUCCESS = "success";

    /** Model attr message type message. */
    static final String MODEL_ATTR_MESSAGE = "message";

    /** Model attr redirect_products type Product list. */
    static final String MODEL_ATTR_REDIRECT_PRODUCTS = "redirect_products";

    /** OrderService. */
    final OrderService orderService;

    /** AuthenticationService. */
    final AuthenticationService authenticationService;

    /**
     * WebshopController constructor.
     *
     * @param orderService          of type OrderService
     * @param authenticationService of type AuthenticationService.
     */
    protected WebshopController(OrderService orderService, AuthenticationService authenticationService) {
        this.orderService = orderService;
        this.authenticationService = authenticationService;
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
            throw new OrderInvalidException("Order with status " + order.getStatus() + " is not suitable for checkout");
        }
    }
}
