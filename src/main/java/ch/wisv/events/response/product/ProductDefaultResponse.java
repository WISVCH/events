package ch.wisv.events.response.product;

import ch.wisv.events.data.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Lob;

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
public class ProductDefaultResponse {

    /**
     * Field key of the Product
     */
    @Getter
    private String key;

    /**
     * Field title of the Product
     */
    @Getter
    private String title;

    /**
     * Field description of the Product
     */
    @Lob
    @Getter
    private String description;

    /**
     * Field cost of the Product
     */
    @Getter
    private float cost;

    /**
     * Constructor ProductDefaultResponse creates a new ProductDefaultResponse instance.
     *
     * @param product of type Product
     */
    public ProductDefaultResponse(Product product) {
        this.key = product.getKey();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.cost = product.getCost();
    }
}
