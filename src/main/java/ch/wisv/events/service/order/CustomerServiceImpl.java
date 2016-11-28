package ch.wisv.events.service.order;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.request.sales.SalesOrderCustomerCreateRequest;
import ch.wisv.events.exception.CustomerNotFound;
import ch.wisv.events.exception.InvalidCustomerException;
import ch.wisv.events.exception.RFIDTokenAlreadyUsedException;
import ch.wisv.events.repository.order.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer getByRFIDToken(String token) {
        Optional<Customer> optional = customerRepository.findByRfidToken(token);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new CustomerNotFound("User with RFID token " + token + " not found!");
    }

    @Override
    public Customer createCustomer(SalesOrderCustomerCreateRequest request) {
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
}
