package ch.wisv.events.infrastructure.webshop.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * Order Data Transfer Object.
 */
@Data
public class OrderDto {

    /**
     * List of OrderProductDto.
     */
    @NotNull(message = "No products in this order")
    @Size(min = 1, message = "No products in this order")
    List<OrderProductDto> products;

    /**
     * OrderDto.
     */
    public OrderDto() {
        this.products = new ArrayList<>();
    }
}
