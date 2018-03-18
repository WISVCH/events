package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    /**
     * Find all Ticket by Product and Customer
     *
     * @param product of type Product
     * @param owner   of type Customer
     *
     * @return List<Ticket>
     */
    List<Ticket> findAllByProductAndOwner(Product product, Customer owner);

    /**
     * Find all Ticket by Product
     *
     * @param product of type Product
     *
     * @return List<Ticket>
     */
    List<Ticket> findAllByProduct(Product product);

    /**
     * Find all Ticket by Customer
     *
     * @param owner of type Customer
     *
     * @return List<Ticket>
     */
    List<Ticket> findAllByOwner(Customer owner);

    /**
     * Count all Ticket by Product
     *
     * @param product of type Product
     *
     * @return int
     */
    int countTicketsByProduct(Product product);
}
