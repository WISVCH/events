package ch.wisv.events.service.product;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;

import java.util.Collection;
import java.util.List;

/**
 * ProductService.
 */
public interface ProductService {

    /**
     * Get all products
     *
     * @return List of Products
     */
    List<Product> getAllProducts();

    /**
     * Get all available products
     *
     * @return Collection of Products
     */
    List<Product> getAvailableProducts();

    /**
     * Get Product by Key
     *
     * @param key key of a Product
     * @return Product
     */
    Product getByKey(String key);

    /**
     * Update Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the new product information
     */
    void update(ProductRequest productRequest);

    /**
     * Add a new Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the product information
     */
    void add(ProductRequest productRequest);

    /**
     * Remove a Product
     *
     * @param product Product to be deleted.
     */
    void delete(Product product);

}
