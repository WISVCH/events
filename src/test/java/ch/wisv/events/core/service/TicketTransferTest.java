package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.TicketNotTransferableException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.repository.TicketRepository;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.core.service.ticket.TicketServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

/**
 * TicketService test.
 */
public class TicketTransferTest extends ServiceTest {

    /** Mock of TicketRepository. */
    @Mock
    private TicketRepository ticketRepository;

    /** AuthenticationService. */
    @Mock
    private AuthenticationService authenticationService;

    /** MailService. */
    @Mock
    private MailService mailService;

    /** TicketService. */
    private TicketService ticketService;

    /** Tickets. */
    private Ticket ticket1, ticket2;

    /** Product. */
    private Product product1, product2;

    /** Customers. */
    private Customer customer1, customer2;

    /**
     * Setup for the test class.
     */
    @Before
    public void setUp() {
        ticketService = new TicketServiceImpl(ticketRepository, mailService);

        customer1 = new Customer();
        customer1.setVerifiedChMember(true);

        customer2 = new Customer();

        product1 = new Product();
        product2 = new Product();
        product2.setChOnly(true);

        ticket1 = new Ticket();
        ticket1.setOwner(customer1);
        ticket1.setProduct(product1);
        ticket1.setUniqueCode("uniqueCode1");
        ticket1.setStatus(TicketStatus.OPEN);
        ticket1.setValid(true);

        ticket2 = new Ticket();
        ticket2.setOwner(customer2);
        ticket2.setProduct(product2);
        ticket2.setUniqueCode("uniqueCode2");
        ticket2.setStatus(TicketStatus.SCANNED);
        ticket1.setValid(true);

        // Set current customer to customer1
        Mockito.when(authenticationService.getCurrentCustomer()).thenReturn(customer1);
    }

    /**
     * Test ticket transfer to another customer.
     */
    @Test
    public void transferTicket() {

        when(ticketRepository.findById(any(Integer.class))).thenReturn(Optional.of(ticket1));
        when(ticketRepository.saveAndFlush(any(Ticket.class))).thenReturn(ticket1);

        String uniqueCode = ticket1.getUniqueCode();

        try {
            ticketService.transfer(ticket1, customer1, customer2);
        } catch (TicketNotTransferableException e) {
            fail("Should not have thrown any exception");
        }

        // Check if customer2 is new owner
        assertEquals(customer2, ticket1.getOwner());

        // Check if unique code is changed
        assertNotEquals(uniqueCode, ticket1.getUniqueCode());
    }

    /**
     * Test ticket transfer of scanned ticket.
     */
    @Test
    public void transferScannedTicket() {

        when(ticketRepository.findById(any(Integer.class))).thenReturn(Optional.of(ticket2));
        when(ticketRepository.saveAndFlush(any(Ticket.class))).thenReturn(ticket2);

        TicketNotTransferableException thrown = assertThrows(TicketNotTransferableException.class, () -> ticketService.transfer(ticket2, customer1, customer2));
        assert(thrown.getMessage().contains("Ticket is already scanned."));

    }

    /**
     * Test ticket transfer of invalid ticket.
     */
    @Test
    public void transferInvalidTicket() {
        ticket1.setValid(false);

        TicketNotTransferableException thrown = assertThrows(TicketNotTransferableException.class, () -> ticketService.transfer(ticket1, customer1, customer2));
        assert (thrown.getMessage().contains("Ticket is not valid."));
    }

    /**
     * Test ticket transfer of ticket that has a product linked to an expired event.
     */
    @Test
    public void transferTicketWithExpiredEvent() {
        Event event = new Event();
        event.setEnding(LocalDateTime.now().minusDays(1));

        product1.setEvent(event);

        TicketNotTransferableException thrown = assertThrows(TicketNotTransferableException.class, () -> ticketService.transfer(ticket1, customer1, customer2));
        assert (thrown.getMessage().contains("Related event has already passed."));
    }

    /**
     * Test transferring ChOnly ticket to customer that is not a verified Ch member.
     */
    @Test
    public void transferChOnlyTicket() {
        ticket1.setProduct(product2);

        TicketNotTransferableException thrown = assertThrows(TicketNotTransferableException.class, () -> ticketService.transfer(ticket1, customer1, customer2));
        assert (thrown.getMessage().contains("Ticket can only be transferred to a verified CH member."));
    }

    /**
     * Test transferring a ticket that is now owned by the current customer.
     */
    @Test
    public void transferNotOwnedTicket() {
        TicketNotTransferableException thrown = assertThrows(TicketNotTransferableException.class, () -> ticketService.transfer(ticket1, customer2, customer1));
        assert (thrown.getMessage().contains("Ticket can only be transferred to the owner."));
    }

    /**
     * Test transferring a ticket that is already owned by the current customer.
     */
    @Test
    public void transferSelfTicket() {
        TicketNotTransferableException thrown = assertThrows(TicketNotTransferableException.class, () -> ticketService.transfer(ticket1, customer1, customer1));
        assert (thrown.getMessage().contains("Sadly you can not transfer a ticket to yourself.."));
    }
}