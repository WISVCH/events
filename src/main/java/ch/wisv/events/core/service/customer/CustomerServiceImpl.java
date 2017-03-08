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
import org.springframework.util.StringUtils;

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
     * Add a new customer.
     *
     * @param customer customer model
     */
    @Override
    public void create(Customer customer) {
        this.checkRequiredFields(customer);
        customerRepository.saveAndFlush(customer);
    }

    /**
     * Update a existing customer.
     *
     * @param customer customer model
     */
    @Override
    public void update(Customer customer) {
        this.checkRequiredFields(customer);
        Customer model = this.getByKey(customer.getKey());

        model.setChUsername(model.getChUsername());
        model.setName(model.getName());
        model.setEmail(model.getEmail());

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
     * @param model of type Customer
     * @throws InvalidCustomerException      when one of the required fields is not valid
     * @throws RFIDTokenAlreadyUsedException when the rfid token is already in use
     */
    private void checkRequiredFields(Customer model) throws InvalidCustomerException {
        if (model == null) throw new InvalidCustomerException("Customer can not be null!");

        String[][] check = new String[][]{
                {model.getName(), "name"},
                {model.getEmail(), "email"},
                {model.getRfidToken(), "RFID token"}
        };
        this.checkFieldsEmpty(check);

        if (customerRepository.findAll().stream().anyMatch(x -> !x.getKey().equals(model.getKey())
                && x.getRfidToken().equals(model.getRfidToken()))) {
            throw new RFIDTokenAlreadyUsedException("RFID token is already used!");
        }
    }

    /**
     * Checks if the a field in the String[][] is empty. If so it will throw an exception
     *
     * @param fields of type String[][]
     * @throws InvalidCustomerException when one of the fields in empty
     */
    private void checkFieldsEmpty(String[][] fields) throws InvalidCustomerException {
        for (String[] field : fields) {
            if (field[0] == null || field[0].equals("")) {
                throw new InvalidCustomerException(StringUtils.capitalize(field[1]) + " is empty, but a required "
                        + "field, so please fill in this field!");
            }
        }
    }
}
