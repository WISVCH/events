package ch.wisv.events.service;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by sven on 14/10/2016.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product getProductByKey(String key) {
        Optional<Product> productOptional = productRepository.findByKey(key);
        if (productOptional.isPresent()) {
            return productOptional.get();
        }
        return null;
    }
}
