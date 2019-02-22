package ch.wisv.events.infrastructure.webshop.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * OrderProduct Data Transfer Object.
 */
@Data
public class OrderProductDto {

    /**
     * Product key.
     */
    @NotEmpty(message = "Product cannot be empty")
    private String productKey;

    /**
     * ProductOption key.
     */
    private String productOptionKey;

    /**
     * Amount.
     */
    @Min(value = 1, message = "Amount should be at least 1")
    private int amount;
}
