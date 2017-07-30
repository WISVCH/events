package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.product.Product;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
@Data
public class SoldProduct {

    /**
     * ID of the sold product
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Key of the sold product
     */
    private String key;

    /**
     * Product that is sold
     */
    @ManyToOne
    private Product product;

    /**
     * Order of the sold product
     */
    @ManyToOne
    private Order order;

    /**
     * Customer who bought the product
     */
    @ManyToOne
    private Customer customer;

    /**
     * Status of the sold product
     */
    private SoldProductStatus status;

    /**
     * Default constructor
     */
    public SoldProduct() {
        this.key = UUID.randomUUID().toString();
        this.status = SoldProductStatus.OPEN;
    }

    /**
     * Constructor with Customer, Product and Order
     *
     * @param customer Customer
     * @param product  Product
     * @param order    Order
     */
    public SoldProduct(Customer customer, Product product, Order order) {
        this();
        this.customer = customer;
        this.product = product;
        this.order = order;
    }

}
