package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import java.util.List;

/**
 * TicketService.
 */
public interface TicketService {

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
}
