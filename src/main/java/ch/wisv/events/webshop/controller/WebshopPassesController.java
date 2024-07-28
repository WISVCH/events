package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.exception.normal.TicketPassFailedException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/passes")
public class WebshopPassesController extends WebshopController {
    /** TicketService. */
    private final TicketService ticketService;

    /**
     * @param authenticationService of type AuthenticationService
     * @param orderService          of type OrderService
     * @param ticketService         of type TicketService
     */
    public WebshopPassesController(
            AuthenticationService authenticationService,
            OrderService orderService,
            TicketService ticketService
    ) {
        super(orderService, authenticationService);
        this.ticketService = ticketService;
    }

    /**
     * Get wallet pass of ticket.
     */
    @GetMapping("/apple/{key}/wallet.pkpass")
    public void getApplePass(HttpServletResponse response, @PathVariable  String key) throws IOException {
        Customer customer = authenticationService.getCurrentCustomer();

        try {
            Ticket ticket = ticketService.getByKey(key);

            if (!ticket.owner.equals(customer) && ticket.owner.isVerifiedChMember()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            byte[] bytes = ticketService.getApplePass(ticket);

            response.setContentType("application/vnd.apple.pkpass");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.getOutputStream().close();
        }
        catch (TicketNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (TicketPassFailedException | IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Redirect the user to the Google Pass URL.
     */
    @GetMapping("/google/{key}")
    public void getGooglePass(HttpServletResponse response, @PathVariable String key) throws IOException {
        Customer customer = authenticationService.getCurrentCustomer();

        try {
            Ticket ticket = ticketService.getByKey(key);

            if (!ticket.owner.equals(customer) && ticket.owner.isVerifiedChMember()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String link = ticketService.getGooglePass(ticket);
            response.sendRedirect(link);
        }
        catch (TicketNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (TicketPassFailedException | IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
