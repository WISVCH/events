package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import java.util.Objects;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SalesScanEventController.
 */
@Controller
@RequestMapping(value = "/sales/scan/event/{key}")
@PreAuthorize("hasRole('USER')")
public class SalesScanEventController {

    /** Unique code length. */
    private static final int UNIQUE_CODE_LENGTH = 6;

    /** Attribute event. */
    private static final String ATTR_EVENT = "event";

    /** Attribute error. */
    private static final String ATTR_ERROR = "error";

    /** Default return redirect on error. */
    private static final String ERROR_REDIRECT = "redirect:/sales/scan/";

    /** EventService. */
    private final EventService eventService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * SalesScanEventController.
     *
     * @param eventService  of type EventService
     * @param ticketService of type TicketService
     */
    public SalesScanEventController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    /**
     * View to scan a ticket/code for an event.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param method   of type String
     *
     * @return String
     */
    @GetMapping("/{method}")
    public String scanner(Model model, RedirectAttributes redirect, @PathVariable String key, @PathVariable String method) {
        try {
            Event event = eventService.getByKey(key);
            model.addAttribute(ATTR_EVENT, event);

            return "sales/scan/event/" + method;
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute(ATTR_ERROR, e.getMessage());

            return ERROR_REDIRECT;
        }
    }

    /**
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param barcode  of type String
     *
     * @return String
     */
    @PostMapping("/barcode")
    public String barcodeScanner(RedirectAttributes redirect, @PathVariable String key, @RequestParam("barcode") String barcode) {
        String uniqueCode = barcode.substring(barcode.length() - (UNIQUE_CODE_LENGTH - 1), barcode.length() - 1);

        return this.handleScanTicket(redirect, key, uniqueCode, "/sales/scan/event/" + key + "/barcode");
    }

    /**
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param code     of type String
     *
     * @return String
     */
    @PostMapping("/code")
    public String codeScanner(RedirectAttributes redirect, @PathVariable String key, @RequestParam("code") String code) {
        return this.handleScanTicket(redirect, key, code, "/sales/scan/event/" + key + "/code");
    }

    /**
     * Scan a Ticket using a unique code.
     *
     * @param event      of type Event
     * @param uniqueCode of type String
     *
     * @return Ticket
     *
     * @throws EventsException when Ticket is not found or ticket has already been scanned
     */
    private Ticket getTicketByUniqueCode(Event event, String uniqueCode) throws EventsException {
        return event.getProducts().stream()
                .map(product -> {
                    try {
                        return ticketService.getByUniqueCode(product, uniqueCode);
                    } catch (TicketNotFoundException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new TicketNotFoundException("Ticket  " + uniqueCode + " does not exists"));
    }

    /**
     * Handle scan Tickets.
     *
     * @param redirect    of type RedirectAttributes
     * @param key         of type String
     * @param code        of type String
     * @param redirectUrl of type String
     *
     * @return String
     */
    private String handleScanTicket(RedirectAttributes redirect, String key, String code, String redirectUrl) {
        redirect.addFlashAttribute("redirect", redirectUrl);

        if (code.length() != UNIQUE_CODE_LENGTH) {
            redirect.addFlashAttribute(ATTR_ERROR, "Invalid unique code length!");

            return "redirect:/sales/scan/ticket/error";
        }

        try {
            Event event = eventService.getByKey(key);
            Ticket ticket = this.getTicketByUniqueCode(event, code);
            redirect.addFlashAttribute("ticket", ticket);

            if (ticket.getStatus() == TicketStatus.OPEN) {
                ticketService.updateStatus(ticket, TicketStatus.SCANNED);

                return "redirect:/sales/scan/ticket/success";
            } else {
                return "redirect:/sales/scan/ticket/double";
            }
        } catch (EventsException e) {
            redirect.addFlashAttribute(ATTR_ERROR, e.getMessage());

            return "redirect:/sales/scan/ticket/error";
        }
    }

}
