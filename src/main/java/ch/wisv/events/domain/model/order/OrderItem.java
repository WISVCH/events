package ch.wisv.events.domain.model.order;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.product.ProductOption;
import static java.util.Objects.nonNull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Order OrderItem Entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderItem extends AbstractModel {

    /** Product in the Order. */
    @ManyToOne
    private Product product;

    /** Product Option allowed by the Product. */
    @ManyToOne
    private ProductOption productOption;

    /** Price of a single Product. */
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    /** Number of items of the same Product. */
    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private Long amount;

    /**
     * Instantiates a new Order item.
     *
     * @param product the product
     * @param option  the option
     * @param amount  the amount
     */
    public OrderItem(Product product, ProductOption option, Long amount) {
        this.product = product;
        this.productOption = option;
        this.amount = amount;

        this.price = this.product.getPrice();
        if (nonNull(this.productOption)) {
            this.price += this.productOption.getAdditionalPrice();
        }
    }

    /**
     * Increase amount.
     *
     * @param amount the amount
     */
    public void increaseAmount(Long amount) {
        this.amount += amount;
    }
}
