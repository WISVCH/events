package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.utils.LdapGroup;
import java.time.LocalDate;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * DashboardWebhookController class.
 */
@Controller
@RequestMapping("/administrator/penningmeester")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardPenningmeesterController extends DashboardController {

    /** AuthenticationService. */
    private final AuthenticationService authenticationService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * DashboardWebhookController constructor.
     *
     * @param authenticationService of type AuthenticationService
     * @param ticketService         of type TicketService
     */
    @Autowired
    public DashboardPenningmeesterController(
            AuthenticationService authenticationService,
            TicketService ticketService
    ) {
        this.authenticationService = authenticationService;
        this.ticketService = ticketService;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping
    public String index(Model model) {
        Customer customer = authenticationService.getCurrentCustomer();
        if (!customer.getLdapGroups().contains(LdapGroup.PENNINGMEESTER) && !customer.getLdapGroups().contains(LdapGroup.CHBEHEER)) {
            return "redirect:/administrator/";
        }

        model.addAttribute("productMap", this.generateProductMap());

        return "admin/penningmeester/index";
    }

    /**
     * Generate product map on local date.
     *
     * @return HashMap
     */
    private HashMap<LocalDate, HashMap<Product, Integer>> generateProductMap() {
        HashMap<LocalDate, HashMap<Product, Integer>> tickets = new HashMap<>();
        ticketService.getAll().forEach(ticket -> {
            LocalDate date = ticket.getOrder().getPaidAt().toLocalDate();
            date = LocalDate.of(date.getYear(), date.getMonthValue(), 1);

            HashMap<Product, Integer> list = tickets.get(date);
            if (list == null) {
                list = new HashMap<>();
                list.put(ticket.getProduct(), 1);
            } else {
                list.compute(ticket.getProduct(), (k, v) -> {
                    if (v == null) {
                        return 1;
                    } else {
                        return v + 1;
                    }
                });
            }
            tickets.put(date, list);
        });

        return tickets;
    }
}
