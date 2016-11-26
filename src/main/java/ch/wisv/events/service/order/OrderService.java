package ch.wisv.events.service.order;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.model.order.OrderStatus;
import ch.wisv.events.data.request.sales.SalesOrderRequest;

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
public interface OrderService {

    /**
     * @return
     */
    List<Order> getAllOrders();

    /**
     * @param reference
     * @return
     */
    Order getByReference(String reference);

    /**
     * @param orderRequest
     */
    Order createOrder(SalesOrderRequest orderRequest);

    /**
     * @param order
     * @param customer
     */
    void addCustomerToOrder(Order order, Customer customer);

    /**
     * @param orderStatus
     */
    void updateOrderStatus(Order order, OrderStatus orderStatus);

}
