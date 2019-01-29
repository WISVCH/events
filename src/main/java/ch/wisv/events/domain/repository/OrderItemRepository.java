package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.order.OrderItem;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * OrderItemRepository.
 */
@Repository
public interface OrderItemRepository extends AbstractRepository<OrderItem> {

}
