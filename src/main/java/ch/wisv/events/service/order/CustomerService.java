package ch.wisv.events.service.order;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.request.sales.SalesCustomerRequest;

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
     * Get a customer by rfidtoken.
     *
     * @param token rfidtoken
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
     * Create a new customer by SalesCustomerRequest.
     * TODO: should be replaces by add(Customer customer)
     *
     * @param request SalesCustomerRequest
     * @return customer
     */
    Customer create(SalesCustomerRequest request);

    /**
     * Update a existing customer.
     *
     * @param model customer model
     */
    void update(Customer model);

    /**
     * Add a new customer.
     *
     * @param model customer model
     */
    void add(Customer model);

    /**
     * Delete a customer.
     *
     * @param customer customer model
     */
    void delete(Customer customer);

}
