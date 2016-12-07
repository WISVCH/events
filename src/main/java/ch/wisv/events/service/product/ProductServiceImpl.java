package ch.wisv.events.service.product;

import ch.wisv.events.data.factory.product.ProductRequestFactory;
import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;
import ch.wisv.events.exception.ProductInUseException;
import ch.wisv.events.exception.ProductNotFound;
import ch.wisv.events.repository.product.ProductRepository;
import ch.wisv.events.service.event.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductServiceImpl.
 */
@Service
public class ProductServiceImpl implements ProductService {

    /**
     * ProductRepository
     */
    private final ProductRepository productRepository;

    /**
     * EventService
     */
    private final EventService eventService;

    /**
     * Default constructor
     *
     * @param productRepository ProductRepository
     * @param eventService      EventService
     */
    public ProductServiceImpl(ProductRepository productRepository, EventService eventService) {
        this.productRepository = productRepository;
        this.eventService = eventService;
    }

    /**
     * Get all products
     *
     * @return List of Products
     */
    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    /**
     * Get all available products
     *
     * @return Collection of Products
     */
    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findAllBySellStartBeforeAndSellEndAfter(LocalDateTime.now(), LocalDateTime.now())
                                .stream().filter(x -> x.getSold() < x.getMaxSold())
                                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get Product by Key
     *
     * @param key key of a Product
     * @return Product
     */
    @Override
    public Product getByKey(String key) {
        Optional<Product> product = productRepository.findByKey(key);
        if (product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFound("Product with key " + key + " not found!");
    }

    /**
     * Update Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the new product information
     */
    @Override
    public void update(ProductRequest productRequest) {
        Product product = productRepository.findById(productRequest.getId());
        product = ProductRequestFactory.update(product, productRequest);

        productRepository.save(product);
    }

    /**
     * Add a new Product using a ProductRequest
     *
     * @param productRequest ProductRequest containing the product information
     */
    @Override
    public void add(ProductRequest productRequest) {
        Product product = ProductRequestFactory.create(productRequest);

        productRepository.save(product);
    }

    /**
     * Remove a Product
     *
     * @param product Product to be deleted.
     */
    @Override
    public void delete(Product product) {
        List<Event> events = eventService.getEventByProductKey(product.getKey());
        if (events.size() > 0) {
            throw new ProductInUseException("Product is already added to an Event");
        }
        productRepository.delete(product);
    }

}
