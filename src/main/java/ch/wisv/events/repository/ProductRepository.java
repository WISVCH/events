package ch.wisv.events.repository;

import ch.wisv.events.data.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * ProductRepository
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find a Product by id
     *
     * @param id id of a Product
     * @return Product
     */
    Product findById(Long id);

    /**
     * Find a Product by key
     *
     * @param key key of a Product
     * @return Product
     */
    Optional<Product> findByKey(String key);

    /**
     * Find Products by after selling date and before
     */
    Collection<Product> findAllBySellStartBeforeAndSellEndAfter(LocalDateTime sellStart, LocalDateTime sellEnd);

}
