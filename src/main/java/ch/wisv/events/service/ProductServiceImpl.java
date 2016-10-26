package ch.wisv.events.service;

import ch.wisv.events.data.factory.product.ProductRequestFactory;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;
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

    /**
     * Update Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the new product information
     */
    @Override
    public void updateProduct(ProductRequest productRequest) {
        Product product = productRepository.findById(productRequest.getId());
        product = ProductRequestFactory.update(product, productRequest);

        productRepository.save(product);
    }

    @Override
    public void addProduct(ProductRequest productRequest) {
        Product product = ProductRequestFactory.create(productRequest);

        productRepository.saveAndFlush(product);
    }
}
