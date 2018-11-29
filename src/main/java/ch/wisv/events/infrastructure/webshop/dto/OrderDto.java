package ch.wisv.events.infrastructure.webshop.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Order Data Transfer Object.
 */
@Data
public class OrderDto {

    /**
     * List of OrderProductDto.
     */
    List<OrderProductDto> products;

    /**
     * OrderDto.
     */
    public OrderDto() {
        this.products = new ArrayList<>();
    }
}
