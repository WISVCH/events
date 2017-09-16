package ch.wisv.events.core.service.customer;

import ch.wisv.events.core.model.order.Customer;

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
public interface CustomerService {

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    List<Customer> getAllCustomers();

    /**
     * Get a customer by rfidToken.
     *
     * @param token of type String
     * @return Customer
     */
    Customer getByRFIDToken(String token);

    /**
     * Get a customer by key.
     *
     * @param key key
     * @return Customer
     */
    Customer getByKey(String key);

    /**
     * Get a Customer by email.
     *
     * @param email of type String
     * @return Customer
     */
    Customer getByEmail(String email);

    /**
     * Update a existing customer.
     *
     * @param customer customer model
     */
    void update(Customer customer);

    /**
     * Add a new customer.
     *
     * @param customer customer model
     */
    void create(Customer customer);

    /**
     * Delete a customer.
     *
     * @param customer customer model
     */
    void delete(Customer customer);
}
