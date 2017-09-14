package ch.wisv.events.sales.order.service;

import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;

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
public interface SalesAppOrderService {

    /**
     * Method assertAmountOfProductLeft.
     *
     * @param order    of type Order
     * @param customer of type Customer
     */
    void addCustomerToOrder(Order order, Customer customer);

    /**
     * Method create ...
     *
     * @param order of type Order
     */
    void create(Order order);

    /**
     * Method update ...
     *
     * @param order of type Order
     */
    void update(Order order);
}
