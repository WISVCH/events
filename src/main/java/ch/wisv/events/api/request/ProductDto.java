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

    /**
     * (Optional) Sell start of the product.
     */
    private String sellStart;

    /**
     * (Optional) Sell end of the product.
     */
    private String sellEnd;

    /**
     * (Optional) link to redirect buyer to after the buy went though
     */
    private String redirectUrl;

    /**
     * If the product is only for CH members.
     */
    @NotNull
    private boolean chOnly;

    /**
     * This product can be reserved instead of paid directly at checkout.
     */
    @NotNull
    private boolean reservable;

    /**
     * If the product is including association registration.
     */
    @NotNull
    private boolean includesRegistration;
}
