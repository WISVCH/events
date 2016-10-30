package ch.wisv.events.service;

import ch.wisv.events.data.factory.product.ProductRequestFactory;
import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;
import ch.wisv.events.exception.ProductInUseException;
import ch.wisv.events.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sven on 14/10/2016.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final EventService eventService;

    public ProductServiceImpl(ProductRepository productRepository, EventService eventService) {
        this.productRepository = productRepository;
        this.eventService = eventService;
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product getProductByKey(String key) {
        return productRepository.findByKey(key);
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

    @Override
    public void deleteProduct(Product product) {
        List<Event> events = eventService.getEventByProductKey(product.getKey());
        if (events.size() > 0) {
            throw new ProductInUseException("Product is already added to an Event");
        }
        productRepository.delete(product);
    }
}
