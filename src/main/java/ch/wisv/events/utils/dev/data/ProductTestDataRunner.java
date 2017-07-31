package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.ProductRepository;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Component
@Profile("dev")
@Order(value = 1)
public class ProductTestDataRunner extends TestDataRunner {

    /**
     * Field eventRepository
     */
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
     * Method loop
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Product product = this.createProduct(jsonObject);

        this.productRepository.save(product);
    }

    /**
     * Method createProduct ...
     *
     * @param jsonEvent of type JSONObject
     * @return Product
     */
    private Product createProduct(JSONObject jsonEvent) {
        int days = df.getNumberBetween(1, 10);

        return new Product(
                (String) jsonEvent.get("title"),
                (String) jsonEvent.get("description"),
                (Double) jsonEvent.get("cost"),
                ((Long) jsonEvent.get("maxSold")).intValue(),
                LocalDateTime.now().plusDays(days).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(days).plusHours(1).truncatedTo(ChronoUnit.MINUTES)
        );
    }
}
