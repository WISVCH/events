package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SalesScanEventController.
 */
@Controller
@RequestMapping(value = "/sales/scan/ticket")
@PreAuthorize("hasRole('USER')")
public class SalesScanTicketController {

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * SalesScanEventController.
     *
     * @param ticketService of type TicketService
     */
    public SalesScanTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Ticket index view.
     *
     * @param model  of type Model
     *
     * @return String
     */
    @GetMapping("/error")
    public String error(Model model) {
        if (!model.containsAttribute("error")) {
            return "redirect:/sales/scan/";
        }

        if (!model.containsAttribute("redirect")) {
            model.addAttribute("redirect", "/sales/scan/");
        }

        return "sales/scan/ticket/error";
    }

    /**
     * Ticket index view.
     *
     * @param model  of type Model
     * @param status of type String
     *
     * @return String
     */
    @GetMapping("/{status}")
    public String index(Model model, @PathVariable String status) {
        if (!model.containsAttribute("ticket")) {
            return "redirect:/sales/scan/";
        }

        if (!model.containsAttribute("redirect")) {
            model.addAttribute("redirect", "/sales/scan/");
        }

        return "sales/scan/ticket/" + status;
    }

}
