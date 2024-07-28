package ch.wisv.events.api.controller;

import ch.wisv.events.api.request.ProductDto;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.util.Search;
import ch.wisv.events.core.webhook.WebhookPublisher;
import static ch.wisv.events.utils.ResponseEntityBuilder.createResponseEntity;
import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductRESTController.
 */
@RestController
@RequestMapping({"/api/v1/products","/api/v1/products/"})
public class ProductRestController {

    /**
     * ProductService.
     */
    private final ProductService productService;

    /** WebhookPublisher. */
    private final WebhookPublisher webhookPublisher;

    /**
     * Default constructor.
     *
     * @param productService ProductService
     * @param webhookPublisher of type WebhookPublisher
     */
    public ProductRestController(ProductService productService, WebhookPublisher webhookPublisher) {
        this.productService = productService;
        this.webhookPublisher = webhookPublisher;
    }

    /**
     * Create a Product based on a ProductDto and return the id, key and title of this Product.
     *
     * @param product of type ProductDto
     *
     * @return ResponseEntity
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createProduct(@Validated @RequestBody ProductDto product) {
        try {
            Product created = productService.create(product);
            webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_CREATE_UPDATE, created);

            JSONObject json = new JSONObject();
            json.put("product_id", created.getId());
            json.put("product_key", created.getKey());
            json.put("product_title", created.getTitle());

            return createResponseEntity(HttpStatus.CREATED, "Product successfully created", json);
        } catch (ProductInvalidException e) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Get all unused products into search format.
     *
     * @param query query
     *
     * @return Search Object
     */
    @GetMapping({"/search/unused","/search/unused/"})
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
