package ch.wisv.events.data.model.order;

import ch.wisv.events.data.model.product.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sven on 17/10/2016.
 */
@Entity
public class Order {

    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @Getter
    private OrderStatus status;

    @Getter
    @Setter
    private float amount;

    @Getter
    @ManyToMany(targetEntity = Product.class)
    private List<Product> products;

    @Getter
    private String publicReference;

    @Getter
    private LocalDateTime creationDate;

    @Getter
    @Setter
    private LocalDateTime paidDate;

    @Getter
    @Setter
    @ManyToOne
    private Customer customer;

    public Order() {
        this.status = OrderStatus.OPEN;
        this.products = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
        this.publicReference = UUID.randomUUID().toString();
    }

    /**
     * Add product to Order and add cost to product
     *
     * @param product Product
     */
    public void addProduct(Product product) {
        this.products.add(product);
        this.amount += product.getCost();
    }

    /**
     * Set order status and update the sold foreach product
     *
     * @param status
     */
    public void setStatus(OrderStatus status) {
        OrderStatus old = this.status;
        if (!old.toString().contains("PAID") && status.toString().contains("PAID")) {
            this.getProducts().forEach(x -> x.setSold(x.getSold() + 1));
        } else if (old.toString().contains("PAID") && !status.toString().contains("PAID")) {
            this.getProducts().forEach(x -> x.setSold(x.getSold() - 1));
        }

        this.status = status;
    }

}
