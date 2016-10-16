package ch.wisv.events.dashboard.controller;

import ch.wisv.events.event.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sven on 15/10/2016.
 */
@Controller
@RequestMapping("/dashboard/tickets")
public class DashboardTicketController {

    private final TicketService ticketService;

    @Autowired
    public DashboardTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tickets", ticketService.getAllTickets());
        return "dashboard/tickets/index";
    }

}
