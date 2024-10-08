package ch.wisv.events.core.service.product;

import ch.wisv.events.api.request.ProductDto;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import java.util.List;

/**
 * ProductService interface.
 */
public interface ProductService {

    /**
     * Get all products.
     *
     * @return List of Products
     */
    List<Product> getAllProducts();

    /**
     * Get all available products.
     *
     * @return Collection of Products
     */
    List<Product> getAvailableProducts();

    /**
     * Get possible parent products.
     *
     * @return Collection of Products
     */
    List<Product> getPossibleParentProductsByProduct(Product product);

    /**
     * Get the parent, children, and sibling products, including the original product
     *
     * @return Collection of Products
     */
    List<Product> getRelatedProducts(Product product);

    /**
     * Get Product by Key.
     *
     * @param key key of a Product
     *
     * @return Product
     *
     * @throws ProductNotFoundException when Product is not found
     */
    Product getByKey(String key) throws ProductNotFoundException;

    /**
     * Add a new Product using a Product.
     *
     * @param product of type Product
     *
     * @return Product
     *
     * @throws ProductInvalidException when Product is invalid
     */
    Product create(Product product) throws ProductInvalidException;

    /**
     * Create a new Product from a ProductDTO.
     *
     * @param productDto of type ProductDto
     *
     * @return Product
     *
     * @throws ProductInvalidException when Product is invalid
     */
    Product create(ProductDto productDto) throws ProductInvalidException;

    /**
     * Update Product using a Product.
     *
     * @param product Product containing the new product information
     *
     * @throws ProductNotFoundException when Product is not found
     * @throws ProductInvalidException  when Product is invalid
     */
    void update(Product product) throws ProductNotFoundException, ProductInvalidException;

    /**
     * Remove a Product.
     *
     * @param product Product to be deleted.
     */
    void delete(Product product);

    /**
     * Change Product sold count.
     *
     * @param order            of type Order
     * @param reservationCount of type boolean
     * @param decrease         of type boolean
     */
    void increaseProductCount(Order order, boolean reservationCount, boolean decrease);

}
