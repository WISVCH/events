package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.user.User;
import java.util.List;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;

/**
 * OrderRepository.
 */
@Repository
public interface OrderRepository extends AbstractRepository<Order> {

    List<Order> findAllByCustomerAndItemsContainsProduct(User customer, Product product);
}
