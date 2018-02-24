package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;

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
public interface TicketService {

    /**
     * Create a Ticket by an OrderProduct.
     *
     * @param order        of type Order
     * @param orderProduct of type OrderProduct
     * @return Ticket
     */
    Ticket createByOrderProduct(Order order, OrderProduct orderProduct);

    /**
     * Get all Ticket by a Product and Customer
     *
     * @param product  of type Product
     * @param customer of type Customer
     * @return List<Ticket>
     */
    List<Ticket> getAllByProductAndCustomer(Product product, Customer customer);

    /**
     * Get all Ticket by a Product
     *
     * @param product of type Product
     * @return List<Ticket>
     */
    List<Ticket> getAllByProduct(Product product);

    /**
     * Get all Ticket by a Customer
     *
     * @param customer of type Customer
     * @return List<Ticket>
     */
    List<Ticket> getAllByCustomer(Customer customer);

    /**
     * Get all Ticket by Product
     *
     * @param product of type Product
     * @return int
     */
    int countByProduct(Product product);
}
