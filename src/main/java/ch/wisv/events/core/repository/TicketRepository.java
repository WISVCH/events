package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TicketRepository interface.
 */
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    /**
     * Find all Ticket by Product and Customer.
     *
     * @param product of type Product
     * @param owner   of type Customer
     *
     * @return List
     */
    List<Ticket> findAllByProductAndOwner(Product product, Customer owner);

    /**
     * Find all Ticket by Product.
     *
     * @param product of type Product
     *
     * @return List
     */
    List<Ticket> findAllByProduct(Product product);

    /**
     * Find all Ticket by Customer.
     *
     * @param owner of type Customer
     *
     * @return List
     */
    List<Ticket> findAllByOwner(Customer owner);

    /**
     * Find all Ticket by Order.
     *
     * @param order of type Order
     *
     * @return List
     */
    List<Ticket> findAllByOrder(Order order);

    /**
     * Check if there exists a Ticket with a Product and a Unique code.
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     *
     * @return boolean
     */
    boolean existsByProductAndUniqueCode(Product product, String uniqueCode);

    /**
     * Find a Ticket by Product and UniqueCode.
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     *
     * @return Optional
     */
    Optional<Ticket> findByProductAndUniqueCode(Product product, String uniqueCode);

    /**
     * Find Ticket by key.
     *
     * @param key of type String
     *
     * @return Optional
     */
    Optional<Ticket> findByKey(String key);
}
