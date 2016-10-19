package ch.wisv.events.event.controller;

import ch.wisv.events.event.model.Product;
import ch.wisv.events.event.model.ProductSearch;
import ch.wisv.events.event.repository.EventRepository;
import ch.wisv.events.event.repository.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sven on 14/10/2016.
 */
@RestController
@RequestMapping(value = "/products")
public class ProductRESTController {

    private final ProductRepository productRepository;

    private final EventRepository eventRepository;

    public ProductRESTController(ProductRepository productRepository, EventRepository eventRepository) {
        this.productRepository = productRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/unused/search")
    public ProductSearch getSearchProducts(@RequestParam(value = "query", required = false) String query) {
        List<Product> productList = productRepository.findAll();
        ProductSearch search = new ProductSearch(query);

        if (query != null) {
            List<Product> filterProduct = productList.stream()
                                                     .filter(p -> eventRepository.findAllByProductsId(p.getId()).size() < 1)
                                                     .filter(p -> p.getTitle().toLowerCase()
                                                                .contains(query.toLowerCase()))
                                                     .collect(Collectors.toCollection(ArrayList::new));
            for (Product product : filterProduct) {
                search.addItem(product.getTitle(), product.getId());
            }
        }

        return search;
    }
}
