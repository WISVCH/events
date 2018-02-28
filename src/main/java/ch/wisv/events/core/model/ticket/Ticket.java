package ch.wisv.events.core.model.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.product.Product;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
     * Unique code of the ticket, if the
     */
    @NotNull
    public String uniqueCode;

    /**
     * Status of the Ticket (e.g. Open, Scanned, ...)
     */
    @NotNull
    public TicketStatus status;

    /**
     * Set if the ticket valid
     */
    private boolean valid;

    /**
     * Default constructor of Ticket
     */
    public Ticket() {
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
        super();
        this.owner = owner;
        this.product = product;
        this.uniqueCode = uniqueCode;
        this.valid = true;
        this.status = TicketStatus.OPEN;
    }

}
