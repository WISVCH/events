package ch.wisv.events.api.controller;

import ch.wisv.events.api.request.ProductDTO;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.util.Search;
import ch.wisv.events.core.service.product.ProductService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ch.wisv.events.utils.ResponseEntityBuilder.createResponseEntity;

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
     * Default constructor.
     *
     * @param productService ProductService
     */
    public ProductRESTController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createProduct(@Validated @RequestBody ProductDTO product) {
        try {
            Product created = productService.create(product);

            JSONObject json = new JSONObject();
            json.put("product_id", created.getId());
            json.put("product_key", created.getKey());
            json.put("product_title", created.getTitle());

            return createResponseEntity(HttpStatus.CREATED, "Product successfullly created", json);
        } catch (ProductInvalidException e) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Get all unused products into search format.
     *
     * @param query query
     * @return Search Object
     */
    @GetMapping(value = "/search/unused")
    @PreAuthorize("hasRole('ADMIN')")
    public Search getSearchProducts(@RequestParam(value = "query", required = false) String query) {
        List<Product> productList = productService.getAllProducts();
        Search search = new Search(query);

        String finalQuery = (query != null) ? query : "";
        productList.stream()
                .filter(p -> !p.isLinked() && p.getTitle().toLowerCase().contains(finalQuery.toLowerCase()))
                .forEach(x -> search.addItem(x.getTitle(), x.getId()));

        return search;
    }
}
