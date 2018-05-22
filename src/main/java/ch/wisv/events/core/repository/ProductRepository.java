package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductRepository class.
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Find a Product by id.
     *
     * @param id id of a Product
     *
     * @return Product
     */
    Optional<Product> findById(Integer id);

    /**
     * Find a Product by key.
     *
     * @param key key of a Product
     *
     * @return Product
     */
    Optional<Product> findByKey(String key);

    /**
     * Find Products by after selling date and before.
     *
     * @param sellStart of type LocalDateTime
     */
    Collection<Product> findAllBySellStartBefore(LocalDateTime sellStart);

}
