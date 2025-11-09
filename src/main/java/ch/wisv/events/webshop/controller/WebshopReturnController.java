package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * WebshopReturnController class.
 */
@Controller
@RequestMapping("/return/{key}")
public class WebshopReturnController extends WebshopController {
    /** TicketService. */
    final TicketService ticketService;

    /** Attribute to indicate if the order only contains reservable products. */
    private static final String MODEL_ATTR_TICKETS = "tickets";

    /**
     * WebshopReturnController constructor.
     *
     * @param orderService          of type OrderService
     * @param authenticationService of type AuthenticationService
     */
    public WebshopReturnController(OrderService orderService, AuthenticationService authenticationService, TicketService ticketService) {
        super(orderService, authenticationService);
        this.ticketService = ticketService;
    }

    /**
     * Completion page index.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping
    public String returnIndex(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute(MODEL_ATTR_ORDER, order);

            // Get all OrderProducts that have a redirect url.
            List<Product> productsWithRedirect = order.getOrderProducts().stream()
                    .map(OrderProduct::getProduct)
                    .filter(product -> Objects.nonNull(product.getRedirectUrl()) && !product.getRedirectUrl().isEmpty())
                    .collect(Collectors.toList());


            List<Ticket> tickets = this.ticketService.getAllByOrder(order);

            model.addAttribute(MODEL_ATTR_REDIRECT_PRODUCTS, productsWithRedirect);
            model.addAttribute(MODEL_ATTR_TICKETS, tickets);

            switch (order.getStatus()) {
                case PENDING:
                    return "webshop/return/pending";
                case EXPIRED:
                    return "webshop/return/expired";
                case PAID:
                    return "webshop/return/success";
                case CANCELLED:
                    return "webshop/return/cancelled";
                case RESERVATION:
                    return "webshop/return/reservation";
                case ERROR:
                default:
                    return "webshop/return/error";
            }
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Fallback error page.
     * @param message The message to display on the page. If null, a generic message will be displayed.
     * @param model The model to add the message to.
     * @return The error page.
     */
    @GetMapping("/fallback")
    public String fallbackError(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("message", message != null ? message : "Something went wrong. Please try again later.");
        return "error";
    }
}
