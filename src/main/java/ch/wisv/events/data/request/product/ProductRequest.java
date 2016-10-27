package ch.wisv.events.data.request.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;

/**
 * ProductRequest (DATA Class)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    /**
     * ID of a Product
     */
    Long id;

    /**
     * Key, Title, Selling Start, Selling End of a Product
     */
    String key, title, sellStart, sellEnd;

    /**
     * Description of a Product
     */
    @Lob
    String description;

    /**
     * Cost of a Product
     */
    float cost;

    /**
     * Maximum number of products sold of a Product
     */
    Integer maxSold;

}
