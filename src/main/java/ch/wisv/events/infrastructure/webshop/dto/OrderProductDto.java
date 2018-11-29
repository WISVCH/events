package ch.wisv.events.infrastructure.webshop.dto;

import lombok.Data;

/**
 * OrderProduct Data Transfer Object.
 */
@Data
public class OrderProductDto {

    /**
     * Product key.
     */
    private String productKey;

    /**
     * ProductOption key.
     */
    private String productionOptionKey;

    /**
     * Amount.
     */
    private Long amount;
}
