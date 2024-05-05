package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.customer.Customer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;

import ch.wisv.events.core.util.VatRate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "orders")
@Data
@Getter
@Setter
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
     * VAT of the order.
     */
    @NotNull
    private Double vat = 0.0;

    /**
     * Field products list of Products in the Order.
     */
    @ManyToMany(targetEntity = OrderProduct.class, fetch = FetchType.EAGER)
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
     * Tickets have been created.
     */
    private boolean ticketCreated;

    /**
     * CH payments public reference.
     */
    private String chPaymentsReference;

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

        this.setVat(Math.round(this.getOrderProducts().stream()
                .mapToDouble(orderProduct -> orderProduct.getVat() * orderProduct.getAmount()
                ).sum() * 100.0) / 100.0);
    }

    /**
     * Get VAT per Rate.
     */
    public HashMap<VatRate, Double> getTotalVatPerRate() {
        HashMap<VatRate, Double> vatRates = new HashMap<>();

        this.getOrderProducts().forEach(orderProduct -> {
            Double vat = vatRates.getOrDefault(orderProduct.getVatRate(), 0.0);
            vatRates.put(orderProduct.getProduct().getVatRate(), vat + orderProduct.getVat() * orderProduct.getAmount());
        });

        return vatRates;
    }
}
