package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopOrderOverviewController class.
 */
@Controller
@RequestMapping("/overview")
@PreAuthorize("hasRole('ROLE_USER')")
public class WebshopOrderOverviewController extends WebshopController {

    /** Model attribute orders. */
    private static final String MODEL_ATTR_ORDERS = "orders";

    /** Model attribute tickets. */
    private static final String MODEL_ATTR_TICKETS = "tickets";

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * @param authenticationService of type AuthenticationService
     * @param orderService          of type OrderService
     * @param ticketService         of type TicketService
     */
    public WebshopOrderOverviewController(
            AuthenticationService authenticationService,
            OrderService orderService,
            TicketService ticketService
    ) {
        super(orderService, authenticationService);
        this.ticketService = ticketService;
    }

    /**
     * Get list of all the tickets of a user.
     *
     * @param model of type Model.
     *
     * @return String
     */
    @GetMapping
    public String ticketOverview(Model model) {
        Customer customer = authenticationService.getCurrentCustomer();

        model.addAttribute(MODEL_ATTR_CUSTOMER, customer);
        model.addAttribute(MODEL_ATTR_ORDERS, orderService.getReservationByCustomer(customer));
        model.addAttribute(MODEL_ATTR_TICKETS, ticketService.getAllByCustomer(customer));

        return "webshop/overview/index";
    }
}
