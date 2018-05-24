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

@Controller
@RequestMapping("/overview")
@PreAuthorize("hasRole('ROLE_USER')")
public class WebshopOrderOverviewController {

    /**
     *
     */
    private final AuthenticationService authenticationService;

    /**
     *
     */
    private final OrderService orderService;

    /**
     *
     */
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
        this.authenticationService = authenticationService;
        this.orderService = orderService;
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

        model.addAttribute("customer", customer);
        model.addAttribute("orders", orderService.getReservationByCustomer(customer));
        model.addAttribute("tickets", ticketService.getAllByCustomer(customer));

        return "webshop/overview/index";
    }
}
