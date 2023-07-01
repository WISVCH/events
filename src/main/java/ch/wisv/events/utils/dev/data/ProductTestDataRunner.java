package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.ProductRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import ch.wisv.events.core.util.VatRate;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(value = 1)
public class ProductTestDataRunner extends TestDataRunner {

    /** ProductRepository. */
    private final ProductRepository productRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param productRepository of type ProductRepository
     */
    public ProductTestDataRunner(ProductRepository productRepository) {
        this.productRepository = productRepository;

        this.setJsonFileName("products.json");
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     *
     * @return Product
     */
    private Product createProduct(JSONObject jsonObject) {
        int days = df.getNumberBetween(1, 10);

        Product product = new Product(
                (String) jsonObject.get("title"),
                (String) jsonObject.get("description"),
                (Double) jsonObject.get("cost"),
                (VatRate) jsonObject.get("vatRate"),
                ((Long) jsonObject.get("maxSold")).intValue(),
                LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(days).truncatedTo(ChronoUnit.MINUTES)
        );
        product.setKey((String) jsonObject.get("key"));
        product.setMaxSoldPerCustomer(((Long) jsonObject.get("maxSoldPerCustomer")).intValue());

        return product;
    }

    /**
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Product product = this.createProduct(jsonObject);

        this.productRepository.save(product);
    }
}
