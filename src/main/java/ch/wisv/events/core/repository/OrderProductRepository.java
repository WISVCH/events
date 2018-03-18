package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {

}
