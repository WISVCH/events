package ch.wisv.events.service;

import ch.wisv.events.data.model.product.Product;

import java.util.List;

/**
 * Created by sven on 14/10/2016.
 */
public interface ProductService {

    List<Product> getAllProducts();

    Product getProductByKey(String key);
}
