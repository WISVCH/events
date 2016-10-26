package ch.wisv.events.repository;

import ch.wisv.events.data.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by sven on 12/10/2016.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findById(Long orderId);

    Optional<Product> findByKey(String key);

}
