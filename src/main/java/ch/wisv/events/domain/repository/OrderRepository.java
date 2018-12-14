package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.order.Order;
import org.springframework.stereotype.Repository;

/**
 * OrderRepository.
 */
@Repository
public interface OrderRepository extends AbstractRepository<Order> {

}
