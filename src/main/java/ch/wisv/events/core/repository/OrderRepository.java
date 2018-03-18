package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Method findOneByPublicReference find Order by public reference.
     *
     * @param publicReference of type String
     *
     * @return Optional<Order>
     */
    Optional<Order> findOneByPublicReference(String publicReference);

    /**
     * Method findAllByOwner find Order by Customer.
     *
     * @param owner of type Customer
     *
     * @return List<Order>
     */
    List<Order> findAllByOwner(Customer owner);

    /**
     * Find all Order by a Customer that have a given status
     *
     * @param owner  of type Customer
     * @param status of type OrderStatus
     *
     * @return List<Order>
     */
    List<Order> findAllByOwnerAndStatus(Customer owner, OrderStatus status);

}
