package ch.wisv.events.service.product;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;

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
     * Get Product by Key
     *
     * @param key key of a Product
     * @return Product
     */
    Product getProductByKey(String key);

    /**
     * Update Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the new product information
     */
    void updateProduct(ProductRequest productRequest);

    /**
     * Add a new Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the product information
     */
    void addProduct(ProductRequest productRequest);

    /**
     * Remove a Product
     *
     * @param product Product to be deleted.
     */
    void deleteProduct(Product product);

}
