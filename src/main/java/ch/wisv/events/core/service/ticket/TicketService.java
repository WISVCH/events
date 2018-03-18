package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import java.util.List;

public interface TicketService {

    /**
     * Create a Ticket by an OrderProduct.
     *
     * @param order        of type Order
     * @param orderProduct of type OrderProduct
     *
     * @return Ticket
     */
    Ticket createByOrderProduct(Order order, OrderProduct orderProduct);

    /**
     * Get all Ticket by a Product and Customer
     *
     * @param product  of type Product
     * @param customer of type Customer
     *
     * @return List<Ticket>
     */
    List<Ticket> getAllByProductAndCustomer(Product product, Customer customer);

    /**
     * Get all Ticket by a Product
     *
     * @param product of type Product
     *
     * @return List<Ticket>
     */
    List<Ticket> getAllByProduct(Product product);

    /**
     * Get all Ticket by a Customer
     *
     * @param customer of type Customer
     *
     * @return List<Ticket>
     */
    List<Ticket> getAllByCustomer(Customer customer);

    /**
     * Get all Ticket by Product
     *
     * @param product of type Product
     *
     * @return int
     */
    int countByProduct(Product product);
}
