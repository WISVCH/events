package ch.wisv.events.event.service;

import ch.wisv.events.event.model.Product;

import java.util.List;

/**
 * Created by sven on 14/10/2016.
 */
public interface ProductService {

    List<Product> getAllProducts();

    Product getProductByKey(String key);
}
