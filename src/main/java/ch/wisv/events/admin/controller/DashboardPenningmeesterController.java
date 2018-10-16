package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.ticket.TicketService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * DashboardWebhookController constructor.
     *
     * @param ticketService of type TicketService
     */
    @Autowired
    public DashboardPenningmeesterController(TicketService ticketService) {
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
        model.addAttribute("productMap", this.generateProductMap());

        return "admin/penningmeester/index";
    }

    /**
     * Generate product map on local date.
     *
     * @return HashMap
     */
    private Map<LocalDate, Map<Product, Integer>> generateProductMap() {
        Map<LocalDate, Map<Product, Integer>> tickets = new HashMap<>();
        ticketService.getAll().forEach(ticket -> {
            LocalDate date = ticket.getOrder().getPaidAt().toLocalDate();
            date = LocalDate.of(date.getYear(), date.getMonthValue(), 1);

            Map<Product, Integer> list = tickets.getOrDefault(date, new HashMap<Product, Integer>() {{ put(ticket.getProduct(), 0); }});
            list.computeIfPresent(ticket.getProduct(), (k, v) -> v + 1);
            tickets.put(date, list);
        });

        return tickets;
    }
}
