package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.product.Product;
import lombok.Getter;
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
@Entity
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
    @Getter
    private Integer id;

    /**
     * Field status status of the Order.
     */
    @Getter
    @Setter
    private OrderStatus status;

    /**
     * Field amount amount of the Order.
     */
    @Getter
    @Setter
    private float amount;

    /**
     * Field products list of Products in the Order.
     */
    @Getter
    @ManyToMany(targetEntity = Product.class)
    private List<Product> products;

    /**
     * Field publicReference UUID for public reference.
     */
    @Getter
    @Setter
    private String publicReference;

    /**
     * Field creationDate date time on which the order is create.
     */
    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    private LocalDateTime creationDate;

    /**
     * Field paidDate date time on which the order has been paid.
     */
    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    private LocalDateTime paidDate;

    /**
     * Field customer customer that order this.
     */
    @Getter
    @Setter
    @ManyToOne
    private Customer customer;

    /**
     * Constructor Order creates a new Order instance.
     */
    public Order() {
        this.status = OrderStatus.OPEN;
        this.products = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
        this.publicReference = UUID.randomUUID().toString();
    }

    public Order(Customer customer) {
        this();
        this.customer = customer;
    }

    /**
     * Add product to Order and create cost to product.
     *
     * @param product Product
     */
    public void addProduct(Product product) {
        this.products.add(product);
        this.amount += product.getCost();
    }

}
