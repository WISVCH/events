package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.product.Product;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
@NoArgsConstructor
@Entity
@Data
public class OrderProduct {

    /**
     * Field id.
     */
    @Id
    @GeneratedValue
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
    }
}
