package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.customer.Customer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@AllArgsConstructor
@Entity
@Table(name = "\"order\"")
@Data
public class Order {

    /**
     * Field TIME_FORMAT standard time format.
     */
    private final String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * Field id id of the Order.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Field status status of the Order.
     */
    private OrderStatus status;

    /**
     * Field amount amount of the Order.
     */
    private Double amount;

    /**
     * Field products list of Products in the Order.
     */
    @ManyToMany(targetEntity = OrderProduct.class)
    private List<OrderProduct> orderProducts;

    /**
     * Field publicReference UUID for public reference.
     */
    @Column(unique = true)
    private String publicReference;

    /**
     * Field soldBy
     */
    private String createdBy;

    /**
     * Field creationDate date time on which the order is create.
     */
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime creationDate;

    /**
     * Field paidDate date time on which the order has been paid.
     */
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime paidDate;

    /**
     * Field customer customer that order this.
     */
    @ManyToOne
    private Customer customer;

    /**
     * Constructor Order creates a new Order instance.
     */
    public Order() {
        this.status = OrderStatus.OPEN;
        this.creationDate = LocalDateTime.now();
        this.publicReference = UUID.randomUUID().toString();
        this.orderProducts = new ArrayList<>();
    }

    /**
     * Add OrderProduct to the Order.
     *
     * @param orderProduct of type OrderProduct.
     */
    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
    }
}
