package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.webshop.service.PaymentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopPaymentController class.
 */
@Controller
@RequestMapping("/checkout/{key}/payment")
public class WebshopPaymentController extends WebshopController {

    /** Error message order not suitable for checkout. */
    private static final String ERROR_ORDER_NOT_SUITABLE_CHECKOUT = "Order is not suitable for checkout!";

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /** PaymentsService. */
    private final PaymentsService paymentsService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     * @param paymentsService        of type PaymentsService
     * @param authenticationService  of type AuthenticationService
     */
    public WebshopPaymentController(
            OrderService orderService,
            OrderValidationService orderValidationService,
            PaymentsService paymentsService,
            AuthenticationService authenticationService
    ) {
        super(orderService, authenticationService);
        this.orderValidationService = orderValidationService;
        this.paymentsService = paymentsService;
    }

    /**
     * Show different types of Payment options.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String string
     */
    @GetMapping
    public String paymentOverview(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = this.getOrderAndCheck(key);
            model.addAttribute(MODEL_ATTR_ORDER, order);
            model.addAttribute(MODEL_ATTR_CUSTOMER, authenticationService.getCurrentCustomer());

            if (order.getAmount() == 0.d) {
                order.setPaymentMethod(PaymentMethod.OTHER);
                orderService.updateOrderStatus(order, OrderStatus.PAID);

                return "redirect:/return/" + order.getPublicReference();
            }

            if (order.getOwner().getRfidToken() == null || order.getOwner().getRfidToken().equals("")) {
                model.addAttribute(
                        MODEL_ATTR_MESSAGE,
                        "No card linked to your account! Link a card to your account, for an easier and faster check-in at the event(s)."
                );
            }

            return "webshop/payment/index";
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Payment method using a Reservation.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String string
     */
    @GetMapping("/reservation")
    public String paymentReservation(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = this.getOrderAndCheck(key);
            orderService.updateOrderStatus(order, OrderStatus.RESERVATION);

            return "redirect:/return/" + order.getPublicReference() + "/reservation";
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Payment method using iDeal.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String string
     */
    @GetMapping("/ideal")
    public String paymentIdeal(RedirectAttributes redirect, @PathVariable String key) {
        return this.payment(redirect, key, PaymentMethod.IDEAL);
    }

    /**
     * Payment method using SOFORT.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String string
     */
    @GetMapping("/sofort")
    public String paymentSofort(RedirectAttributes redirect, @PathVariable String key) {
        return this.payment(redirect, key, PaymentMethod.SOFORT);
    }

    /**
     * Assert if an Order is suitable for Payment.
     *
     * @param order of type Order
     *
     * @throws OrderInvalidException when Order is not suitable for Payment
     */
    private void assertOrderIsSuitableForPayment(Order order) throws OrderInvalidException {
        if (order.getStatus() != OrderStatus.ASSIGNED && order.getStatus() != OrderStatus.CANCELLED) {
            throw new OrderInvalidException(ERROR_ORDER_NOT_SUITABLE_CHECKOUT);
        }

        if (order.getOrderProducts().size() == 0) {
            throw new OrderInvalidException(ERROR_ORDER_NOT_SUITABLE_CHECKOUT);
        }

        if (order.getOwner() == null) {
            throw new OrderInvalidException(ERROR_ORDER_NOT_SUITABLE_CHECKOUT);
        }

        if (order.getCreatedBy() == null || !order.getCreatedBy().equals("events-webshop")) {
            throw new OrderInvalidException(ERROR_ORDER_NOT_SUITABLE_CHECKOUT);
        }
    }

    /**
     * Get order and check if it is suitable.
     *
     * @param key of type String
     *
     * @return Order
     *
     * @throws OrderNotFoundException when Order is not found
     * @throws OrderInvalidException  when the Order is not suitable for payment
     */
    private Order getOrderAndCheck(String key) throws OrderNotFoundException, OrderInvalidException {
        Order order = orderService.getByReference(key);

        this.assertOrderIsSuitableForCheckout(order);
        this.assertOrderIsSuitableForPayment(order);

        return order;
    }

    /**
     * Handle a type of payment.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param method   of type PaymentMethod
     *
     * @return String
     */
    private String payment(RedirectAttributes redirect, String key, PaymentMethod method) {
        try {
            Order order = this.getOrderAndCheck(key);
            order.setPaymentMethod(method);
            order.setStatus(OrderStatus.PENDING);
            orderService.update(order);
            orderValidationService.assertOrderIsValidForPayment(order);

            return "redirect:" + paymentsService.getPaymentsMollieUrl(order);
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }
}
