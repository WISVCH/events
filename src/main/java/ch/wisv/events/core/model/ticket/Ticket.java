package ch.wisv.events.core.model.ticket;

import ch.wisv.events.core.model.customer.Customer;
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
     * @param owner      of type Customer
     * @param product    of type Product
     * @param uniqueCode of type String
     */
    public Ticket(Customer owner, Product product, String uniqueCode) {
        this.owner = owner;
        this.product = product;
        this.uniqueCode = uniqueCode;
        this.valid = true;
        this.key = UUID.randomUUID().toString();
        this.status = TicketStatus.OPEN;
    }

}
