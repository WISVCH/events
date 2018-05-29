package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.webshop.service.PaymentsService;
import ch.wisv.events.webshop.service.WebshopService;
import static java.lang.Thread.sleep;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopPaymentController class.
 */
@Controller
@RequestMapping("/checkout/{key}/payment")
public class WebshopPaymentController extends WebshopController {

    /** Max retry attempt to fetch payment status. */
    private static final int MAX_PAYMENT_FETCH_ATTEMPT = 5;

    /** Amount of milli secs between status fetch attempt. */
    private static final int WAIT_BETWEEN_STATUS_FETCH_ATTEMPT = 500;

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /** PaymentsService. */
    private final PaymentsService paymentsService;

    /** WebshopService. */
    private final WebshopService webshopService;

    /** MailService. */
    private final MailService mailService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     * @param paymentsService        of type PaymentsService
     * @param webshopService         of type WebshopService
     * @param mailService            of type MailService
     */
    public WebshopPaymentController(
            OrderService orderService,
            OrderValidationService orderValidationService,
            PaymentsService paymentsService,
            WebshopService webshopService,
            MailService mailService
    ) {
        super(orderService);
        this.orderValidationService = orderValidationService;
        this.paymentsService = paymentsService;
        this.webshopService = webshopService;
        this.mailService = mailService;
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
            model.addAttribute("order", order);

            if (order.getAmount() == 0.d) {
                order.setPaymentMethod(PaymentMethod.OTHER);
                orderService.updateOrderStatus(order, OrderStatus.PAID);

                return "redirect:/return/" + order.getPublicReference();
            }

            if (order.getOwner().getRfidToken() == null || order.getOwner().getRfidToken().equals("")) {
                model.addAttribute(
                        "message",
                        "No card linked to your account! Link a card to your account, for an easier and faster check-in at the event(s)."
                );
            }

            return "webshop/payment/index";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
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
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Payment method using iDeal.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String string
     */
    @GetMapping("/ideal")
    public String paymentIdeal(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = this.getOrderAndCheck(key);
            model.addAttribute("model", order);

            order.setPaymentMethod(PaymentMethod.IDEAL);
            order.setStatus(OrderStatus.PENDING);
            orderService.update(order);
            orderValidationService.assertOrderIsValidForIdealPayment(order);

            return "redirect:" + paymentsService.getPaymentsMollieUrl(order);
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Return url after iDeal payment.
     *
     * @param redirect          of type RedirectAttributes
     * @param key               of type String
     * @param paymentsReference of type String
     *
     * @return String string
     */
    @GetMapping("/return")
    public String returnAfterMolliePayment(
            RedirectAttributes redirect, @PathVariable String key, @RequestParam("reference") String paymentsReference
    ) {
        try {
            Order order = orderService.getByReference(key);

            try {
                this.assertOrderIsSuitableForCheckout(order);

                if (order.getStatus() == OrderStatus.PENDING) {
                    this.fetchOrderStatus(order, paymentsReference);

                    return "redirect:/return/" + order.getPublicReference();
                } else {
                    redirect.addFlashAttribute("error", "Order is in an invalid state.");

                    return "redirect:/checkout/" + key + "/payment";
                }
            } catch (PaymentsStatusUnknown | PaymentsConnectionException e) {
                redirect.addFlashAttribute("error", "Something went wrong trying to fetch the payment status.");
                orderService.updateOrderStatus(order, OrderStatus.ASSIGNED);

                return "redirect:/checkout/" + key + "/payment";
            }
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Assert if an Order is suitable for Payment.
     *
     * @param order of type Order
     *
     * @throws OrderInvalidException when Order is not suitable for Payment
     */
    private void assertOrderIsSuitableForPayment(Order order) throws OrderInvalidException {
        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new OrderInvalidException("Order status must be ASSIGNED before payment");
        }

        if (order.getOrderProducts().size() == 0) {
            throw new OrderInvalidException("Order must contain products before payment");
        }

        if (order.getOwner() == null) {
            throw new OrderInvalidException("Order owner must be set before payment");
        }

        if (order.getCreatedBy() == null || !order.getCreatedBy().equals("events-webshop")) {
            throw new OrderInvalidException("Order created by must be set before payment");
        }
    }

    /**
     * Fetch OrderStatus from CH Payments and retry if it fails the first time.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     *
     * @throws EventsException when Payments Status is unknown
     */
    private void fetchOrderStatus(Order order, String paymentsReference) throws EventsException {
        int count = 0;
        int maxCount = MAX_PAYMENT_FETCH_ATTEMPT;

        while (order.getStatus() == OrderStatus.PENDING && count < maxCount) {
            try {
                webshopService.updateOrderStatus(order, paymentsReference);
                sleep(WAIT_BETWEEN_STATUS_FETCH_ATTEMPT);
            } catch (InterruptedException ignored) {
            }

            count++;
        }

        if (count == maxCount) {
            orderService.updateOrderStatus(order, OrderStatus.ERROR);
            mailService.sendErrorPaymentOrder(order);
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
}
