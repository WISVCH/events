package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.model.product.ProductSearch;
import ch.wisv.events.service.EventService;
import ch.wisv.events.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.wisv.events.repository.EventRepository;
import ch.wisv.events.repository.ProductRepository;
import ch.wisv.events.service.EventService;
import ch.wisv.events.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductRESTController.
 */
@RestController
@RequestMapping(value = "/products")
public class ProductRESTController {


    /**
     * ProductService.
     */
    private final ProductService productService;

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * Default constructor.
     *
     * @param productService ProductService
     * @param eventService   EventService
     */
    public ProductRESTController(ProductService productService, EventService eventService) {
        this.productService = productService;
        this.eventService = eventService;
    }

    /**
     * Get request to get all all products
     *
     * @return List of all Products
     */
    @GetMapping(value = "")
    public Collection<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Get all unused products into search format.
     *
     * @param query query
     * @return ProductSearch Object
     */
    @GetMapping(value = "/unused/search")
    public ProductSearch getSearchProducts(@RequestParam(value = "query", required = false) String query) {
        List<Product> productList = productService.getAllProducts();
        ProductSearch search = new ProductSearch(query);

        String finalQuery = (query != null) ? query : "";
        productList.stream()
                   .filter(p -> eventService.getEventByProductKey(p.getKey()).size() < 1)
                   .filter(p -> p.getTitle().toLowerCase().contains(finalQuery.toLowerCase()))
                   .collect(Collectors.toCollection(ArrayList::new))
                   .forEach(x -> search.addItem(x.getTitle(), x.getId()));

        return search;
    }
}
