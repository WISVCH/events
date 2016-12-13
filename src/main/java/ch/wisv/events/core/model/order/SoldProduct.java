package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.product.Product;
import lombok.Getter;
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
public class SoldProduct {

    @Id
    @GeneratedValue
    @Getter
    private Integer id;

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    @ManyToOne
    private Product product;

    @Getter
    @Setter
    @ManyToOne
    private Order order;

    @Getter
    @Setter
    @ManyToOne
    private Customer customer;

    @Getter
    @Setter
    private SoldProductStatus status;

    public SoldProduct() {
        this.key = UUID.randomUUID().toString();
        this.status = SoldProductStatus.OPEN;
    }

}
