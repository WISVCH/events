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
    @NotNull(message = "Order should contain products")
    @Size(min = 1, message = "Order should contain products")
    List<OrderProductDto> products;

    /**
     * OrderDto.
     */
    public OrderDto() {
        this.products = new ArrayList<>();
    }
}
