package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.model.product.ProductSearch;
import ch.wisv.events.repository.EventRepository;
import ch.wisv.events.repository.ProductRepository;
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


    @GetMapping(value = "/unused/search")
    public ProductSearch getSearchProducts(@RequestParam(value = "query", required = false) String query) {
        List<Product> productList = productRepository.findAll();
        ProductSearch search = new ProductSearch(query);

        if (query != null) {
            productList.stream()
                       .filter(p -> eventRepository.findAllByProductsId(p.getId()).size() < 1)
                       .filter(p -> p.getTitle().toLowerCase().contains(query.toLowerCase()))
                       .collect(Collectors.toCollection(ArrayList::new))
                       .forEach(x -> search.addItem(x.getTitle(), x.getId()));
        }

        return search;
    }
}
