package ch.wisv.events.core.service;

import ch.wisv.events.core.exception.CustomerException;
import ch.wisv.events.core.exception.CustomerNotFound;
import ch.wisv.events.core.exception.InvalidCustomerException;
import ch.wisv.events.core.exception.RFIDTokenAlreadyUsedException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.customer.CustomerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
public class CustomerServiceTest extends ServiceTest {

    /**
     * Mock CustomerRepository
     */
    @Mock
    private CustomerRepository repository;

    /**
     * Mock OrderRepository
     */
    @Mock
    private OrderRepository orderRepository;

    /**
     * Field CustomerService;
     */
    private CustomerService customerService;

    /**
     * Customer default
     */
    private Customer customer;

    /**
     *
     */
    @Before
    public void setUp() throws Exception {
        this.customerService = new CustomerServiceImpl(repository, orderRepository);

        this.customer = new Customer("Christiaan Huygens", "events@ch.tudelft.nl", "christiaanh", "12345678");
    }

    /**
     * Test the find customer by rfid token.
     */
    @Test
    public void testFindByRFIDToken() {
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.of(this.customer));

        assertEquals(this.customer, this.customerService.getByRFIDToken("key"));
    }

    /**
     * Test the find customer by rfid token that does not exists
     */
    @Test
    public void testFindByRFIDTokenNotFound() {
        thrown.expect(CustomerNotFound.class);
        thrown.expectMessage("Customer with RFID token key not found!");
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());

        this.customerService.getByRFIDToken("key");
    }

    /**
     * Test get all customers
     */
    @Test
    public void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(Collections.singletonList(this.customer));

        assertEquals(Collections.singletonList(this.customer), this.customerService.getAllCustomers());
    }

    /**
     * Test get all customers with an empty list
     */
    @Test
    public void testGetAllCustomersEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), this.customerService.getAllCustomers());
    }

    /**
     * Test get by key method
     */
    @Test
    public void testGetByKey() {
        when(repository.findByKey(this.customer.getKey())).thenReturn(Optional.of(this.customer));

        assertEquals(this.customer, this.customerService.getByKey(this.customer.getKey()));
    }

    /**
     * Test get by key not found
     */
    @Test
    public void testGetByKeyNotFound() {
        thrown.expect(CustomerNotFound.class);
        thrown.expectMessage("Customer with key key not found!");
        when(repository.findByKey(anyString())).thenReturn(Optional.empty());

        this.customerService.getByKey("key");
    }

    /**
     * Test create method and verify that saveAndFlush is called once
     */
    @Test
    public void testCreate() {
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        this.customerService.create(this.customer);
        verify(repository, times(1)).saveAndFlush(any(Customer.class));
    }

    /**
     * Test create method when name is null
     */
    @Test
    public void testCreateMissingName() {
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Name is empty, but a required field, so please fill in this field!");
        this.customer.setName(null);

        this.customerService.create(this.customer);
    }

    /**
     * Test create method when email is null
     */
    @Test
    public void testCreateMissingEmail() {
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Email is empty, but a required field, so please fill in this field!");
        this.customer.setEmail(null);

        this.customerService.create(this.customer);
    }

    /**
     * Test create method when RFID token is null
     */
    @Test
    public void testCreateMissingRFIDToken() {
        Customer duplicate = new Customer("Constantijn Huygens", "events@ch.tudelft.nl", "constantijnh", "12345678");
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.of(this.customer));
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        thrown.expect(RFIDTokenAlreadyUsedException.class);
        thrown.expectMessage("RFID token is already used!");

        this.customerService.create(duplicate);
    }

    /**
     * Test update method
     */
    @Test
    public void testUpdate() {
        Customer update = new Customer();
        update.setKey(this.customer.getKey());
        update.setRfidToken("123");
        update.setChUsername("test");
        update.setEmail("test@test.com");
        update.setName("test test");

        Customer mock = new Customer();
        when(repository.findByKey(this.customer.getKey())).thenReturn(Optional.of(mock));
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());

        this.customerService.update(update);
        verify(repository, times(1)).save(mock);

        assertEquals(update.getName(), mock.getName());
        assertEquals(update.getEmail(), mock.getEmail());
        assertEquals(update.getChUsername(), mock.getChUsername());
        assertEquals(update.getRfidToken(), mock.getRfidToken());
    }

    /**
     * Test delete method when customer did not place an order
     */
    @Test
    public void testDelete() {
        when(orderRepository.findByCustomer(this.customer)).thenReturn(Collections.emptyList());

        this.customerService.delete(this.customer);
        verify(repository, times(1)).delete(this.customer);
    }

    /**
     * Test delete when customer placed an order
     */
    @Test
    public void testDeleteCustomerPlacedOrders() {
        when(orderRepository.findByCustomer(this.customer)).thenReturn(Collections.singletonList(any(Order.class)));

        thrown.expect(CustomerException.class);
        thrown.expectMessage("Customer has already placed orders, so it can not be deleted!");

        this.customerService.delete(this.customer);
    }
}