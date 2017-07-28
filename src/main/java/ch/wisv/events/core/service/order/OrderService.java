package ch.wisv.events.core.service.order;

import ch.wisv.events.app.request.OrderRequest;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
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
    Order getByReference(String reference);


    /**
     * Method getOrdersByProduct returns list of orders with a certain product in it.
     *
     * @param product of type Product
     * @return List<Order>
     */
    List<Order> getOrdersByProduct(Product product);

    /**
     * Method create creates a new order by OrderRequest.
     *
     * @param orderRequest of type OrderRequest
     * @return Order
     */
    Order create(OrderRequest orderRequest);


    /**
     * Method addCustomerToOrder will create a customer to an order.
     *
     * @param order    of type Order
     * @param customer of type Customer
     */
    void addCustomerToOrder(Order order, Customer customer);


    /**
     * Method updateOrderStatus will update the order status and update the product count.
     *
     * @param order       of type Order
     * @param orderStatus of type OrderStatus
     */
    void updateOrderStatus(Order order, OrderStatus orderStatus);
}
