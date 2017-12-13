package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.order.OrderStatus;

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
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return the allOrders (type List<Order>) of this OrderService object.
     */
    List<Order> getAllOrders();

    /**
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     * @return Order
     */
    Order getByReference(String reference) throws OrderNotFoundException;

    /**
     * Method create creates and order.
     *
     * @param order of type Order
     */
    void create(Order order) throws OrderInvalidException, EventNotFoundException;

    /**
     * Method update ...
     *
     * @param order of type Order
     */
    void update(Order order) throws OrderInvalidException;

    /**
     * Method updateOrderStatus
     *  @param order  of type Order
     * @param status of type OrderStatus
     */
    void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException;

    /**
     * Assert if the Order is valid.
     *
     * @param order of type Order
     */
    void assertIsValid(Order order) throws OrderInvalidException;

    /**
     * Assert if the Order is valid for a Customer.
     *
     * @param order of type order.
     */
    void assertIsValidForCustomer(Order order) throws OrderInvalidException;

    /**
     * Create an Order form a OrderProductDTO.
     *
     * @param orderProductDTO of type OrderProductDTO
     * @return Order
     */
    Order createOrderByOrderProductDTO(OrderProductDTO orderProductDTO) throws ProductNotFoundException;
}
