package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.OrderExceedCustomerLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedEventLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedProductLimitException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.customer.Customer;
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
public interface OrderValidationService {

    /**
     * Assert if an Order is valid.
     *
     * @param order of type Order when the Order is invalid
     */
    void assertOrderIsValid(Order order) throws OrderInvalidException, OrderExceedEventLimitException, OrderExceedProductLimitException;

    /**
     * Assert if an Order is valid of a given Customer.
     *
     * @param order    of type Order
     * @param customer of type Customer
     *
     * @throws OrderInvalidException when the Order is invalid for the Customer
     */
    void assertOrderIsValidForCustomer(Order order, Customer customer) throws OrderInvalidException, OrderExceedCustomerLimitException;

    /**
     * Assert if an Order is valid to go to the payment process
     *
     * @param order of type Order
     */
    void assertOrderIsValidForPayment(Order order) throws OrderInvalidException;
}
