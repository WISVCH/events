package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.TicketNotTransferableException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** CustomerService. */
    private final CustomerService customerService;

    /**
     * @param authenticationService of type AuthenticationService
     * @param orderService          of type OrderService
     * @param ticketService         of type TicketService
     */
    public WebshopTicketController(
            AuthenticationService authenticationService,
            CustomerService customerService,
            OrderService orderService,
            TicketService ticketService
    ) {
        super(orderService, authenticationService);
        this.customerService = customerService;
        this.ticketService = ticketService;
    }

    /** Get ticket transfer page.
     * @param model    of type Model
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/{key}/transfer")
    public String getTransferPage(Model model, RedirectAttributes redirect, @PathVariable String key) {
        Customer customer = authenticationService.getCurrentCustomer();

        try {
            Ticket ticket = ticketService.getByKey(key);

            if(!ticket.canTransfer(customer))
                throw new TicketNotTransferableException();

            model.addAttribute(MODEL_ATTR_CUSTOMER, customer);
            model.addAttribute(MODEL_ATTR_TICKET, ticket);

            return "webshop/tickets/transfer";
        }
        catch (TicketNotTransferableException e) {
            redirect.addFlashAttribute("MODEL_ATTR_ERROR", "Ticket is not transferable");
            return "redirect:/overview";
        }
        catch (Exception e) {
            return "redirect:/overview";
        }
    }

    /** Transfer ticket.
     * @param model    of type Model
     * @param key      of type String
     *
     * @return String
     */
    @PostMapping("/{key}/transfer")
    public String transferTicket(Model model, RedirectAttributes redirect, @PathVariable String key, @RequestParam("email") String email) {
        Customer customer = authenticationService.getCurrentCustomer();

        try {
            Ticket ticket = ticketService.getByKey(key);

            if(!ticket.canTransfer(customer))
                throw new TicketNotTransferableException();

            // Check if customer with email exists
            Customer transferCustomer = customerService.getByEmail(email);

            ticketService.transfer(ticket, transferCustomer);

            redirect.addFlashAttribute("MODEL_ATTR_SUCCESS", "Ticket has been transferred to " + transferCustomer.getEmail());

            return "redirect:/overview";
        }
        catch (TicketNotTransferableException e) {
            redirect.addFlashAttribute("MODEL_ATTR_ERROR", "Ticket is not transferable");
            return "redirect:/overview";
        }
        catch (CustomerNotFoundException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, "Customer with email " + email + " does not exist.");
            return "redirect:/tickets/" + key + "/transfer";
        }
        catch (Exception e) {
            return "redirect:/overview";
        }
    }

}
