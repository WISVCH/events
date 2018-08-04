package ch.wisv.events.api.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * ProductDto object.
 */
@Getter
@Setter
public class ProductDto {

    /**
     * Title of a Product.
     */
    @NotNull
    private String title;

    /**
     * (Optional) Description of a Product.
     */
    private String description;

    /**
     * Cost of a Product.
     */
    @NotNull
    private Double cost;

    /**
     * (Optional) Max sold of a Product.
     */
    private Integer maxSold;

    /**
     * (Optional) Max sold per Customer of a Product.
     */
    private Integer maxSoldPerCustomer;
}
