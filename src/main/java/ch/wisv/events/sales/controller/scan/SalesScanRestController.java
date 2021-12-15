package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import static ch.wisv.events.utils.ResponseEntityBuilder.createResponseEntity;
import java.util.Objects;

import ch.wisv.events.sales.model.ScanDto;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SalesScanRestController.
 */
@RestController
@RequestMapping(value = "/api/v1/sales/scan/event/{key}")
@PreAuthorize("hasRole('USER')")
public class SalesScanRestController {

    /** Unique code length. */
    private static final int UNIQUE_CODE_LENGTH = 6;

    /** Barcode length. */
    private static final int BARCODE_LENGTH = 13;

    /** EventService. */
    private final EventService eventService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * SalesScanRestController.
     *
     * @param eventService  of type EventService
     * @param ticketService of type TicketService
     */
    public SalesScanRestController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    /**
     * @param key     of type String
     * @param barcode of type String
     *
     * @return String
     */
    @PostMapping("/barcode")
    public ResponseEntity barcodeScanner(@PathVariable String key, @RequestParam("barcode") String barcode) {
        if (barcode.length() != BARCODE_LENGTH) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, "Invalid EAN 13 barcode length!");
        }

        String uniqueCode = barcode.substring(barcode.length() - (UNIQUE_CODE_LENGTH + 1), barcode.length() - 1);

        return this.handleScanTicket(key, uniqueCode);
    }

    /**
     * @param key  of type String
     * @param code of type String
     *
     * @return String
     */
    @PostMapping("/code")
    public ResponseEntity codeScanner(@PathVariable String key, @RequestParam("code") String code) {
        if (code.length() != UNIQUE_CODE_LENGTH) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, "Invalid unique code length!");
        }

        return this.handleScanTicket(key, code);
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
                .orElseThrow(() -> new TicketNotFoundException("Ticket " + uniqueCode + " does not exists"));
    }

    /**
     * Handle scan Tickets.
     *
     * @param key  of type String
     * @param code of type String
     *
     * @return String
     */
    private ResponseEntity handleScanTicket(String key, String code) {
        JSONObject json = new JSONObject();

        try {
            Event event = eventService.getByKey(key);
            Ticket ticket = this.getTicketByUniqueCode(event, code);
            ScanDto scan = new ScanDto(ticket.getProduct().getTitle(), ticket.getOwner().getName());

            json.put("ticket", scan);

            if (ticket.getStatus() == TicketStatus.OPEN) {
                ticketService.updateStatus(ticket, TicketStatus.SCANNED);

                return createResponseEntity(HttpStatus.OK, "Ticket scan successful", json);
            } else {
                return createResponseEntity(HttpStatus.ALREADY_REPORTED, "Ticket has already been scanned", json);
            }
        } catch (EventsException e) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
