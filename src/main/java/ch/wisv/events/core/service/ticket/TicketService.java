package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.exception.normal.TicketNotTransferableException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import java.util.List;

/**
 * TicketService interface.
 */
public interface TicketService {

    /**
     * Get ticket by unique code.
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     *
     * @return Ticket
     *
     * @throws TicketNotFoundException when ticket is not found
     */
    Ticket getByUniqueCode(Product product, String uniqueCode) throws TicketNotFoundException;

    /**
     * Get ticket by key.
     *
     * @param key of type String
     *
     * @return Ticket
     *
     * @throws TicketNotFoundException when ticket is not found
     */
    Ticket getByKey(String key) throws TicketNotFoundException;

    /**
     * Get all Ticket by a Product and Customer.
     *
     * @param product  of type Product
     * @param customer of type Customer
     *
     * @return List of Tickets
     */
    List<Ticket> getAllByProductAndCustomer(Product product, Customer customer);

    /**
     * Get all Ticket by a Product.
     *
     * @param product of type Product
     *
     * @return List of Tickets
     */
    List<Ticket> getAllByProduct(Product product);

    /**
     * Get all Ticket by a Customer.
     *
     * @param customer of type Customer
     *
     * @return List of Tickets
     */
    List<Ticket> getAllByCustomer(Customer customer);

    /**
     * Get all Tickets by a Order.
     *
     * @param order of type Order
     *
     * @return List of Tickets
     */
    List<Ticket> getAllByOrder(Order order);

    /**
     * Create tickets by an Order.
     *
     * @param order of type Order
     *
     * @return List of Tickets
     */
    List<Ticket> createByOrder(Order order);

    /**
     * Delete tickets by an Order.
     *
     * @param order of type Order
     */
    void deleteByOrder(Order order);

    /**
     * Update the Ticket status.
     *
     * @param ticket of type Ticket
     * @param status of type TicketStatus
     */
    void updateStatus(Ticket ticket, TicketStatus status);

    /**
     * Get all Ticket.
     *
     * @return List of Ticket
     */
    List<Ticket> getAll();

    /**
     * Transfer a Ticket to another Customer.
     * @param currentCustomer of type Customer
     * @param newCustomer of type Customer
     */
    void transfer(Ticket ticket, Customer currentCustomer, Customer newCustomer) throws TicketNotTransferableException;

}
