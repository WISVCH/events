package ch.wisv.events.domain.model.order;

import ch.wisv.events.domain.converter.ZonedDateTimeConverter;
import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.user.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Order entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractModel {

    /**
     * Customer of the Order.
     */
    @ManyToOne(targetEntity = User.class)
    private User customer;

    /**
     * Total price of the Order.
     */
    @NotNull(message = "Total price can not be null")
    private Double totalPrice;

    /**
     * List of items in the Order.
     */
    @Size(message = "Order should contain at least 1 product", min = 1)
    @OneToMany(targetEntity = OrderItem.class, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Name of the instance that created the order.
     */
    @NotNull(message = "Created by cannot be null")
    private String createdBy;

    /**
     * DateTime when the Order has been paid.
     */
    @Convert(converter = ZonedDateTimeConverter.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private ZonedDateTime paidAt;

    /**
     * Status of the Order.
     */
    @NotNull(message = "Order status cannot be null")
    private OrderStatus status = OrderStatus.OPEN;

    /**
     * Payment method: CASH, CARD or MOLLIE.
     */
    private PaymentMethod paymentMethod;

    /**
     * CH payments public reference.
     */
    private String chPaymentsReference;

    /**
     * Add items.
     *
     * @param item of type OrderItem
     */
    public void addItem(OrderItem item) {
        List<OrderItem> filterItems = this.items.stream()
                .filter(it -> it.getProduct().getPublicReference().equals(item.getProduct().getPublicReference()))
                .filter(it -> it.getProductOption().getPublicReference().equals(item.getProductOption().getPublicReference()))
                .collect(Collectors.toList());

        if (filterItems.size() > 0) {
            filterItems.get(0).increaseAmount(item.getAmount());
        } else {
            this.items.add(item);
        }
    }
}
