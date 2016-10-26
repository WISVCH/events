package ch.wisv.events.data.factory.product;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
public class ProductRequestFactory {

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static ProductRequest create(Product product) {
        return new ProductRequest(
                product.getId(),
                product.getKey(),
                product.getTitle(),
                product.getSellStart().toString(),
                product.getSellEnd().toString(),
                product.getDescription(),
                product.getCost(),
                product.getMaxSold()
        );
    }

    public static Product create(ProductRequest request) {
        return null;
    }

    public static Product update(Product product, ProductRequest request) {
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setSellStart(LocalDateTime.parse(request.getSellStart(), format));
        product.setSellEnd(LocalDateTime.parse(request.getSellEnd(), format));
        product.setCost(request.getCost());
        product.setMaxSold(request.getMaxSold());

        return product;
    }

}
