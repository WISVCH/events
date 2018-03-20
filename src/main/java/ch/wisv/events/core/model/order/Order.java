package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.customer.Customer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Data
public class Order {

    /**
     * Field id id of the Order.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Field publicReference UUID for public reference.
     */
    @Column(unique = true)
    @NotNull
    private String publicReference;

    /**
     * Field customer customer that order this.
     */
    @ManyToOne
    @NotNull
    private Customer owner;

    /**
     * Field amount amount of the Order.
     */
    @NotNull
    private Double amount;

    /**
     * Field products list of Products in the Order.
     */
    @ManyToMany(targetEntity = OrderProduct.class)
    private List<OrderProduct> orderProducts;

    /**
     * Field createdBy the name of the person who created the order.
     */
    @NotNull
    private String createdBy;

    /**
     * Field creationDate date time on which the order is create.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @NotNull
    private LocalDateTime createdAt;

    /**
     * Field paidDate date time on which the order has been paid.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime paidAt;

    /**
     * Field status status of the Order.
     */
    @NotNull
    private OrderStatus status;

    /**
     * Field status status of the Order.
     */
    private PaymentMethod paymentMethod;

    /**
     * Constructor Order creates a new Order instance.
     */
    public Order() {
        this.publicReference = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.ANONYMOUS;
        this.orderProducts = new ArrayList<>();
    }

    /**
     * Add OrderProduct to the Order.
     *
     * @param orderProduct of type OrderProduct.
     */
    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        this.updateOrderAmount();
    }

    /**
     * Calculate and set the order amount.
     */
    public void updateOrderAmount() {
        this.setAmount(
                this.getOrderProducts().stream()
                        .mapToDouble(orderProduct -> orderProduct.getProduct().getCost() * orderProduct.getAmount())
                        .sum()
        );
    }
}
