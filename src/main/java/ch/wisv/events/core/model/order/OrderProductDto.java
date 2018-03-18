package ch.wisv.events.core.model.order;

import java.util.HashMap;
import lombok.Data;

@Data
public class OrderProductDto {

    /**
     * Field product should contain Product keys and number of webshop of that product.
     */
    HashMap<String, Long> products;

    /**
     * Constructor.
     */
    public OrderProductDto() {
        this.products = new HashMap<>();
    }
}
