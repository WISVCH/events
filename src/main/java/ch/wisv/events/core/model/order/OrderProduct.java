package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.product.Product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import ch.wisv.events.core.util.VatRate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Data
public class OrderProduct {

    /**
     * Field id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_product_seq")
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Field product.
     */
    @ManyToOne
    private Product product;

    /**
     * Field price.
     */
    @NotNull
    private Double price;

    /**
     * Field vat.
     */
    @NotNull
    private Double vat = 0.0;

    /**
     * Field vatRate.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private VatRate vatRate;

    /**
     * Field amount.
     */
    @NotNull
    private Long amount;

    /**
     * Construct of a OrderProduct.
     *
     * @param product of type Product
     * @param price   of type Double
     * @param amount  of type Long
     */
    public OrderProduct(Product product, Double price, Long amount) {
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.vat = Math.round(price / (100 + product.getVatRate().getVatRate()) * product.getVatRate().getVatRate() * 100.0) / 100.0;
        this.vatRate = product.getVatRate();
    }
}
