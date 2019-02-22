package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.user.User;
import java.util.List;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * OrderRepository.
 */
@Repository
public interface OrderRepository extends AbstractRepository<Order> {

    @Query("SELECT u FROM Order u WHERE u.customer = ?0 AND u.items IN (SELECT v FROM OrderItem v WHERE v.product = ?1)")
    List<Order> findAllByCustomerAndItemsContainsProduct(User customer, Product product);
}
