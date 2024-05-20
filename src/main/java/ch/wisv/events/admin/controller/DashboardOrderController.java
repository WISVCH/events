package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * DashboardOrderController class.
 */
@Controller
@RequestMapping(value = "/administrator/orders")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardOrderController extends DashboardController {

    /** OrderService. */
    private final OrderService orderService;

    /** MailService. */
    private final MailService mailService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * DashboardOrderController constructor.
     *
     * @param orderService  of type OrderService
     * @param mailService   of type MailService
     * @param ticketService of type TicketService
     */
    public DashboardOrderController(
            OrderService orderService,
            MailService mailService,
            TicketService ticketService
    ) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.ticketService = ticketService;
    }

    /**
     * Show list of all orders.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute(OBJ_ORDERS, this.orderService.getLimitedOrders());

        return "admin/orders/index";
    }

    /**
     * Get a view of an order.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/view/{key}","/view/{key}/"})
    public String edit(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute(OBJ_ORDER, order);

            return "admin/orders/view";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/orders/";
        }
    }

    /**
     * Set the order status to REJECTED, "deleting" the error.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/delete/{key}","/delete/{key}/"})
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            orderService.updateOrderStatus(order, OrderStatus.REJECTED);

            redirect.addFlashAttribute(FLASH_SUCCESS, "Order #" + order.getId() + " has been rejected!");
        } catch (EventsException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());
        }

        return "redirect:/administrator/orders/";
    }

    /**
     * Resend confirmation mail.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/resend-confirmation-mail/{key}","/resend-confirmation-mail/{key}/"})
    public String resendConfirmationMail(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            mailService.sendOrderConfirmation(order, ticketService.getAllByOrder(order));

            redirect.addFlashAttribute(FLASH_SUCCESS, "Order confirmation mail send!");
        } catch (EventsException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());
        }

        return "redirect:/administrator/orders/view/" + key;
    }

    /**
     * Approve a reservation.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param payment  of type PaymentMethod
     *
     * @return String
     */
    @GetMapping({"/approve/{key}/{payment}","/approve/{key}/{payment}/"})
    public String approve(RedirectAttributes redirect, @PathVariable String key, @PathVariable PaymentMethod payment) {
        try {
            Order order = orderService.getByReference(key);
            order.setPaymentMethod(payment);
            orderService.updateOrderStatus(order, OrderStatus.PAID);

            redirect.addFlashAttribute(FLASH_SUCCESS, "Order #" + order.getId() + " has been approved!");
        } catch (EventsException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());
        }

        return "redirect:/administrator/orders/";
    }
}
