package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopOrderOverviewController class.
 */
@Controller
@RequestMapping("/tickets")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class WebshopTicketController extends WebshopController {
    /** Model attribute tickets. */
    private static final String MODEL_ATTR_TICKET = "ticket";

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * @param authenticationService of type AuthenticationService
     * @param orderService          of type OrderService
     * @param ticketService         of type TicketService
     */
    public WebshopTicketController(
            AuthenticationService authenticationService,
            OrderService orderService,
            TicketService ticketService
    ) {
        super(orderService, authenticationService);
        this.ticketService = ticketService;
    }

    /** Get ticket transfer page.
     * @param model    of type Model
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/{key}/transfer")
    public String getTransferPage(Model model, @PathVariable String key) {
        Customer customer = authenticationService.getCurrentCustomer();

        try {
            Ticket ticket = ticketService.getByKey(key);

            model.addAttribute(MODEL_ATTR_CUSTOMER, customer);
            model.addAttribute(MODEL_ATTR_TICKET, ticket);

            return "webshop/tickets/transfer";
        }
        catch (Exception e) {
            return "redirect:/overview";
        }
    }
}
