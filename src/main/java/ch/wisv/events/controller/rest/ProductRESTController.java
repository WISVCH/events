package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.model.product.ProductSearch;
import ch.wisv.events.service.event.EventService;
import ch.wisv.events.service.product.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * ProductRESTController.
 */
@RestController
@RequestMapping("/api/v1/products")
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
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ProductSearch getSearchProducts(@RequestParam(value = "query", required = false) String query) {
        List<Product> productList = productService.getAllProducts();
        ProductSearch search = new ProductSearch(query);

        String finalQuery = (query != null) ? query : "";
        productList.stream()
                   .filter(p -> eventService.getEventByProductKey(p.getKey()).size() < 1)
                   .filter(p -> p.getTitle().toLowerCase().contains(finalQuery.toLowerCase()))
                   .forEach(x -> search.addItem(x.getTitle(), x.getId()));

        return search;
    }
}
