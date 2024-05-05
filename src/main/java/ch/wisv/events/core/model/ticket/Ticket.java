package ch.wisv.events.core.model.ticket;

import ch.wisv.events.core.exception.normal.TicketNotTransferableException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
public class Ticket {

    /**
     * ID of the ticket, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    public Integer id;

    /**
     * Unique code of the ticket.
     */
    @NotNull
    public String key;

    /**
     * Customer which owns the Ticket.
     */
    @ManyToOne(cascade = {jakarta.persistence.CascadeType.ALL})
    public Order order;

    /**
     * Customer which owns the Ticket.
     */
    @ManyToOne
    public Customer owner;

    /**
     * Product to which the Ticket is linked.
     */
    @ManyToOne
    @NotNull
    public Product product;

    /**
     * Unique code of the ticket.
     */
    @NotNull
    public String uniqueCode;

    /**
     * Status of the Ticket (e.g. Open, Scanned, ...)
     */
    @NotNull
    public TicketStatus status;

    /**
     * Set if the ticket valid.
     */
    private boolean valid;

    /**
     * Ticket constructor.
     */
    public Ticket() {
        this.key = UUID.randomUUID().toString();
        this.status = TicketStatus.OPEN;
    }

    /**
     * Constructor of Ticket with Customer, Product and String.
     *
     * @param order      of type Order
     * @param owner      of type Customer
     * @param product    of type Product
     * @param uniqueCode of type String
     */
    public Ticket(Order order, Customer owner, Product product, String uniqueCode) {
        this();
        this.order = order;
        this.owner = owner;
        this.product = product;
        this.uniqueCode = uniqueCode;
        this.valid = true;
    }

    /**
     * Can the ticket be transferred to another customer by the given customer.
     * This is only possible if the ticket is not scanned and not already transferred and if the ticket is valid.
     * Tickets of CH Only products can only be transferred to Verified CH customers.
     * The ticket can be transferred if the current customer is the owner of the ticket or if the current customer is an admin.
     * @param currentCustomer of type Customer
     * @param newCustomer of type Customer
     */
    public boolean canTransfer(Customer currentCustomer, Customer newCustomer, Event event) throws TicketNotTransferableException {
        // Check if the ticket is not scanned and not already transferred and if the ticket is valid.
        if(this.status != TicketStatus.OPEN)
            throw new TicketNotTransferableException("Ticket is already scanned.");

        if(!this.valid)
            throw new TicketNotTransferableException("Ticket is not valid.");

        // Check when the ticket product is linked to an event if that event has not passed.
        if(event != null && LocalDateTime.now().isAfter(event.getEnding()))
            throw new TicketNotTransferableException("Related event has already passed.");

        // Check if the ticket is a CH Only product and if the new customer is not a verified CH customer.
        if(newCustomer != null && this.product.isChOnly() && !newCustomer.isVerifiedChMember())
            throw new TicketNotTransferableException("Ticket can only be transferred to a verified CH member.");

        // Check if the current customer is the owner of the ticket
        if (!this.owner.equals(currentCustomer))
            throw new TicketNotTransferableException("Ticket can only be transferred to the owner.");

        // Check if ticket is not transferred to the same customer.
        if(this.owner.equals(newCustomer))
            throw new TicketNotTransferableException("Sadly you can not transfer a ticket to yourself.. Lezen is adten.");

        return true;
    }
}
