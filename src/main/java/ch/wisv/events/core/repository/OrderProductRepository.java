package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {
    /**
     * Find Order by Product.
     *
     * @param product of type Product
     *
     * @return List
     */
    List<OrderProduct> findAllByProduct(Product product);
}
