package ch.wisv.events.core.service.customer;

import ch.wisv.events.core.exception.CustomerException;
import ch.wisv.events.core.exception.CustomerNotFound;
import ch.wisv.events.core.exception.InvalidCustomerException;
import ch.wisv.events.core.exception.RFIDTokenAlreadyUsedException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
@Service
public class CustomerServiceImpl implements CustomerService {

    /**
     * Field customerRepository.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Field orderRepository.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Get a customer by rfidToken.
     *
     * @param token of type String
     * @return Customer
     */
    @Override
    public Customer getByRFIDToken(String token) {
        Optional<Customer> optional = customerRepository.findByRfidToken(token);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new CustomerNotFound("Customer with RFID token " + token + " not found!");
    }

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Get a customer by key.
     *
     * @param key key
     * @return Customer
     */
    @Override
    public Customer getByKey(String key) {
        Optional<Customer> optional = customerRepository.findByKey(key);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new CustomerNotFound("Customer with key " + key + " not found!");
    }

    /**
     * Get a Customer by email.
     *
     * @param email of type String
     * @return Customer
     */
    @Override
    public Customer getByEmail(String email) {
        Optional<Customer> customer = this.customerRepository.findByEmail(email);

        return customer.orElseThrow(() -> new CustomerNotFound("Customer with email " + email + " not found!"));
    }

    /**
     * Add a new customer.
     *
     * @param customer customer model
     */
    @Override
    public void create(Customer customer) {
        this.assertIsValidCustomer(customer);

        customerRepository.saveAndFlush(customer);
    }

    /**
     * Update a existing customer.
     *
     * @param customer customer model
     */
    @Override
    public void update(Customer customer) {
        this.assertIsValidCustomer(customer);
        Customer model = this.getByKey(customer.getKey());

        model.setChUsername(customer.getChUsername());
        model.setName(customer.getName());
        model.setEmail(customer.getEmail());
        model.setRfidToken(customer.getRfidToken());

        customerRepository.save(model);
    }

    /**
     * Delete a customer.
     *
     * @param customer customer model
     */
    @Override
    public void delete(Customer customer) {
        List<Order> orders = orderRepository.findByCustomer(customer);
        if (orders.size() > 0) {
            throw new CustomerException("Customer has already placed orders, so it can not be deleted!");
        }
        customerRepository.delete(customer);
    }

    /**
     * Will check all the required fields if they are valid.
     *
     * @param customer of type Customer
     * @throws InvalidCustomerException      when one of the required fields is not valid
     * @throws RFIDTokenAlreadyUsedException when the rfid token is already in use
     */
    private void assertIsValidCustomer(Customer customer) throws InvalidCustomerException {
        if (customer == null) {
            throw new InvalidCustomerException("Customer can not be null!");
        }

        if (customer.getName() == null || customer.getName().equals("")) {
            throw new InvalidCustomerException("Name is empty, but a required field, so please fill in this field!");
        }

        if (customer.getEmail() == null || customer.getEmail().equals("")) {
            throw new InvalidCustomerException("Email is empty, but a required field, so please fill in this field!");
        }

        if (!customer.getRfidToken().equals("") && customerRepository.findByRfidToken(customer.getRfidToken()).isPresent()) {
            throw new RFIDTokenAlreadyUsedException("RFID token is already used!");
        }

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new RFIDTokenAlreadyUsedException("Email address is already used!");
        }
    }
}
