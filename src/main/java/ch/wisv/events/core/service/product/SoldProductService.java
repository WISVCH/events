package ch.wisv.events.core.service.product;

import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.product.Product;

import java.util.List;

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
public interface SoldProductService {

    /**
     * Get SoldProduct by key
     *
     * @return SoldProduct
     */
    SoldProduct getByKey(String key);

    /**
     * Method getAll returns the all of this SoldProductService object.
     *
     * @return the all (type List<SoldProduct>) of this SoldProductService object.
     */
    List<SoldProduct> getAll();

    /**
     * Method getByProduct find sold products by product.
     *
     * @param product of type Product
     * @return List<SoldProduct>
     */
    List<SoldProduct> getByProduct(Product product);


    /**
     * Method getAllByCustomerAndProduct find sold products by customer and products.
     *
     * @param customer of type Customer
     * @param product  of type Product
     * @return List<SoldProduct>
     */
    List<SoldProduct> getAllByCustomerAndProduct(Customer customer, Product product);

    /**
     * Method getByCustomer find sold products by customer
     *
     * @param customer of type Customer
     * @return List<SoldProduct>
     */
    List<SoldProduct> getByCustomer(Customer customer);

    /**
     * Method create ...
     *
     * @param order of type Order
     */
    List<SoldProduct> create(Order order);

    /**
     * Method delete ...
     *
     * @param order of type Order
     */
    void delete(Order order);

    /**
     * Update sold product
     *
     * @param soldProduct of type SoldProduct
     */
    void update(SoldProduct soldProduct);
}
