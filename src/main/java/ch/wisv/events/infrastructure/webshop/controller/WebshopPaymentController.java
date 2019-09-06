package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ORDER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_COMPLETE_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_PAYMENT_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_PAYMENT;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.VIEW_WEBSHOP_CHECKOUT_PAYMENT;
import ch.wisv.events.services.OrderService;
import static java.util.Objects.nonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopPaymentController class.
 */
@Controller
@RequestMapping(ROUTE_WEBSHOP_PAYMENT)
public class WebshopPaymentController extends AbstractWebshopController {

    /** OrderService. */
    private final OrderService orderService;

    /**
     * WebshopController constructor.
     *
     * @param orderService of type OrderService
     */
    public WebshopPaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * View the payment options.
     *
     * @param model           of type Model
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/{publicReference}")
    public String viewPaymentOptions(Model model, @PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        if (order.getStatus() == OrderStatus.PAID) {
            return String.format(REDIRECT_COMPLETE_PAGE, order.getPublicReference());
        }

        model.addAttribute(MODEL_ATTR_ORDER, order);

        return VIEW_WEBSHOP_CHECKOUT_PAYMENT;
    }

    /**
     * Checkout using the selected payment method.
     *
     * @param model           of type Model
     * @param publicReference of type String
     * @param method          of type String
     *
     * @return String
     */
    @PostMapping("/{publicReference}/{method}")
    public String paymentMethod(Model model, @PathVariable String publicReference, @PathVariable String method) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        if (order.getStatus() == OrderStatus.PAID) {
            return String.format(REDIRECT_COMPLETE_PAGE, order.getPublicReference());
        }

        return "";
    }
}
