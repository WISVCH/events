package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('USER')")
@RequestMapping({"/sales/sell/payment/{publicReference}","/sales/sell/payment/{publicReference}/"})
public class SalesSellPaymentController {

    /** OrderService. */
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
     * /sales/payment/order/{publicReference}.
     *
     * @param publicReference of type String
     * @param method          of type PaymentMethod
     *
     * @return String
     */
    @PostMapping
    public String payment(
            @PathVariable String publicReference, @RequestParam(value = "method") PaymentMethod method
    ) {
        try {
            Order order = orderService.getByReference(publicReference);
            order.setPaymentMethod((order.getAmount()) == 0.d ? PaymentMethod.OTHER : method);

            orderService.create(order);
            orderService.updateOrderStatus(order, OrderStatus.PAID);

            return "redirect:/sales/sell/order/" + order.getPublicReference() + "/complete";
        } catch (EventsException e) {
            return "redirect:/sales/sell/";
        }
    }
}
