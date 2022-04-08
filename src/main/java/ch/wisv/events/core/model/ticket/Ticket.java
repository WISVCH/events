package ch.wisv.events.core.model.ticket;

import ch.wisv.events.core.exception.normal.TicketNotTransferableException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    @ManyToOne
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
    public void canTransfer(Customer currentCustomer, Customer newCustomer) throws TicketNotTransferableException {
        // Check if the ticket is not scanned and not already transferred and if the ticket is valid.
        if(this.status != TicketStatus.OPEN || !this.valid)
            throw new TicketNotTransferableException("Ticket is already scanned or not valid.");

        // Check if the ticket is a CH Only product and if the new customer is not a verified CH customer.
        if(newCustomer != null && this.product.isChOnly() && !newCustomer.isVerifiedChMember())
            throw new TicketNotTransferableException("Ticket can only be transferred to a verified CH member.");

        // Check if the current customer is the owner of the ticket or if the current customer is an admin.
        if (!this.owner.equals(currentCustomer) && !currentCustomer.isAdmin())
            throw new TicketNotTransferableException("Ticket can only be transferred to the owner.");
    }
}
