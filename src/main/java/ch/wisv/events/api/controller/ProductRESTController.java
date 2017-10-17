package ch.wisv.events.api.controller;

import ch.wisv.events.api.request.ProductDTO;
import ch.wisv.events.api.response.ProductDefaultResponse;
import ch.wisv.events.core.exception.ProductNotFound;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.product.Search;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.ProductService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     * Get request to get all all products.
     *
     * @return List of all Products
     */
    @GetMapping(value = "")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllProducts() {
        return createResponseEntity(HttpStatus.OK, "",
                productService.getAvailableProducts().stream().map(ProductDefaultResponse::new));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createProduct(HttpServletRequest request, @Validated @RequestBody ProductDTO product) {
        Product created = this.productService.create(product);

        JSONObject json = new JSONObject();
        json.put("product_id", created.getId());
        json.put("product_key", created.getKey());
        json.put("product_title", created.getTitle());

        return createResponseEntity(HttpStatus.CREATED, "Product successfullly created", json);
    }

    /**
     * Method getProductByKey get product by key.
     *
     * @param key of type String
     * @return ResponseEntity<?>
     */
    @GetMapping(value = "/{key}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProductByKey(@PathVariable String key) {
        try {
            Product product = productService.getByKey(key);

            return createResponseEntity(HttpStatus.OK, "", new ProductDefaultResponse(product));
        } catch (ProductNotFound e) {
            return createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
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
