package ch.wisv.events.core.service.product;

import ch.wisv.events.api.request.ProductDTO;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.product.Product;

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
    Product getByKey(String key) throws ProductNotFoundException;

    /**
     * Add a new Product using a Product
     *
     * @param product of type Product
     */
    Product create(Product product) throws ProductInvalidException;

    /**
     * Method create ...
     *
     * @param productDTO of type ProductDTO
     */
    Product create(ProductDTO productDTO) throws ProductInvalidException;

    /**
     * Update Product using a Product
     *
     * @param product Product containing the new product information
     */
    void update(Product product) throws ProductNotFoundException, ProductInvalidException;

    /**
     * Remove a Product
     *
     * @param product Product to be deleted.
     */
    void delete(Product product);

}
