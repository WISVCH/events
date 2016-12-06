package ch.wisv.events.service.order;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.request.sales.SalesCustomerRequest;
import ch.wisv.events.exception.CustomerException;
import ch.wisv.events.exception.CustomerNotFound;
import ch.wisv.events.exception.InvalidCustomerException;
import ch.wisv.events.exception.RFIDTokenAlreadyUsedException;
import ch.wisv.events.repository.order.CustomerRepository;
import ch.wisv.events.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
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

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Customer getByRFIDToken(String token) {
        Optional<Customer> optional = customerRepository.findByRfidToken(token);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new CustomerNotFound("Customer with RFID token " + token + " not found!");
    }

    @Override
    public Customer createCustomer(SalesCustomerRequest request) {
        if (request.getCustomerName().equals("") || request.getCustomerEmail().equals("")) {
            throw new InvalidCustomerException("Customer name or email is empty");
        }

        if (customerRepository.findAll().stream().anyMatch(x -> Objects
                .equals(x.getRfidToken(), request.getCustomerRFIDToken()))) {
            throw new RFIDTokenAlreadyUsedException("RFID is already used");
        }

        Customer customer = new Customer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerCHUsername(),
                request.getCustomerRFIDToken()
        );

        customerRepository.saveAndFlush(customer);

        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerByKey(String key) {
        Optional<Customer> optional = customerRepository.findByKey(key);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new CustomerNotFound("Customer with key " + key + " not found!");
    }

    @Override
    public void updateCustomer(Customer model) {
        checkRequiredFields(model);
        Customer vendor = this.getCustomerByKey(model.getKey());

        vendor.setChUsername(model.getChUsername());
        vendor.setName(model.getName());
        vendor.setEmail(model.getEmail());

        customerRepository.save(vendor);
    }

    @Override
    public void addCustomer(Customer model) {
        checkRequiredFields(model);
        customerRepository.saveAndFlush(model);
    }

    @Override
    public void deleteVendor(Customer customer) {
        List<Order> orders = orderRepository.findByCustomer(customer);
        if (orders.size() > 0) {
            throw new CustomerException("Customer has already placed orders, so it can not be deleted!");
        }
        customerRepository.delete(customer);
    }

    private void checkRequiredFields(Customer model) throws InvalidCustomerException {
        String[][] check = new String[][]{
                {model.getName(), "name"},
                {model.getEmail(), "email"},
                {model.getRfidToken(), "RFID token"}
        };
        this.checkFieldsEmpty(check);
    }

    private void checkFieldsEmpty(String[][] fields) throws InvalidCustomerException {
        for (String[] field : fields) {
            if (field[0] == null || field[0].equals("")) {
                throw new InvalidCustomerException(StringUtils.capitalize(field[1]) + " is empty, but a required " +
                        "field, so please fill in this field!");
            }
        }
    }
}
