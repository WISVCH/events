package ch.wisv.events.repository.product;

import ch.wisv.events.data.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

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
    Product findByKey(String key);

}
