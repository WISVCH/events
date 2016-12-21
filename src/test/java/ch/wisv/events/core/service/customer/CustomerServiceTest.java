package ch.wisv.events.core.service.customer;

import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.app.request.CustomerCreateRequest;
import ch.wisv.events.core.exception.CustomerException;
import ch.wisv.events.core.exception.CustomerNotFound;
import ch.wisv.events.core.exception.InvalidCustomerException;
import ch.wisv.events.core.exception.RFIDTokenAlreadyUsedException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.repository.OrderRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class)
@ActiveProfiles("test")
@DataJpaTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TestEntityManager testEntityManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Customer testCustomer;

    @Autowired
    private OrderRepository orderResporitory;

    @Before
    public void setUp() throws Exception {
        testCustomer = new Customer("customer", "email@email.com", "customerc", "01020304");
        testEntityManager.persist(testCustomer);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getByRFIDToken() throws Exception {
        Customer temp = customerService.findByRFIDToken(testCustomer.getRfidToken());

        assertEquals(testCustomer, temp);
    }

    @Test
    public void getByRFIDTokenNotFound() throws Exception {
        String token = "123456789";
        thrown.expect(CustomerNotFound.class);
        thrown.expectMessage("Customer with RFID token " + token + " not found!");

        customerService.findByRFIDToken(token);
    }

    @Test
    public void getByRFIDTokenNull() throws Exception {
        thrown.expect(CustomerNotFound.class);
        thrown.expectMessage("Customer with RFID token " + null + " not found!");

        customerService.findByRFIDToken(null);
    }

    @Test
    public void createRight() throws Exception {
        CustomerCreateRequest customerCreateRequest = new CustomerCreateRequest();
        customerCreateRequest.setCustomerName("test");
        customerCreateRequest.setCustomerEmail("test@test.com");
        customerCreateRequest.setCustomerRFIDToken("02030405");

        Customer customer = customerService.create(customerCreateRequest);
        assertEquals(2, customerService.getAllCustomers().size());
        assertEquals(customerCreateRequest.getCustomerName(), customer.getName());
        assertEquals(customerCreateRequest.getCustomerEmail(), customer.getEmail());
        assertEquals(customerCreateRequest.getCustomerCHUsername(), customer.getChUsername());
        assertNull(customer.getChUsername());
    }

    @Test
    public void createNameOrEmailNull() throws Exception {
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Customer name or email is empty");

        CustomerCreateRequest customerCreateRequest = new CustomerCreateRequest();
        customerCreateRequest.setCustomerName("");
        customerCreateRequest.setCustomerEmail("test@test.com");

        customerService.create(customerCreateRequest);
    }

    @Test
    public void createEmpty() throws Exception {
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Customer name or email is empty");

        customerService.create(new CustomerCreateRequest());
    }

    @Test
    public void createDoubleRFIDToken() throws Exception {
        CustomerCreateRequest customerCreateRequest = new CustomerCreateRequest();
        customerCreateRequest.setCustomerName("test");
        customerCreateRequest.setCustomerEmail("test@test.com");
        customerCreateRequest.setCustomerRFIDToken("01020304");

        thrown.expect(RFIDTokenAlreadyUsedException.class);
        thrown.expectMessage("RFID is already used");

        customerService.create(customerCreateRequest);
    }

    @Test
    public void createNull() throws Exception {
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Customer can not be null");

        customerService.create(null);
    }

    @Test
    public void getAllCustomers() throws Exception {
        assertEquals(1, customerService.getAllCustomers().size());
    }

    @Test
    public void getByKey() throws Exception {
        Customer temp = customerService.getByKey(testCustomer.getKey());

        assertEquals(testCustomer, temp);
    }

    @Test
    public void getByKeyNotFound() throws Exception {
        String key = "123";
        thrown.expect(CustomerNotFound.class);
        thrown.expectMessage("Customer with key " + key + " not found!");

        customerService.getByKey(key);
    }

    @Test
    public void getByKeyNull() throws Exception {
        thrown.expect(CustomerNotFound.class);
        thrown.expectMessage("Customer with key " + null + " not found!");

        customerService.getByKey(null);
    }

    @Test
    public void add() throws Exception {
        Customer customer = new Customer("name", "email", "ch_username", "02030405");

        customerService.add(customer);
        assertEquals(2, customerService.getAllCustomers().size());
        assertEquals(customer, customerService.getByKey(customer.getKey()));
    }


    @Test
    public void addCustomerTwice() throws Exception {
        Customer temp = new Customer("customer", "email@email.com", "customerc", "01020304");
        thrown.expect(RFIDTokenAlreadyUsedException.class);
        thrown.expectMessage("RFID token is already used!");

        customerService.add(temp);

        assertEquals(1, customerService.getAllCustomers().size());
    }

    @Test
    public void addCustomerWithSameKey() throws Exception {
        Customer customer = new Customer("name", "email", "ch_username", "01020304");
        thrown.expect(RFIDTokenAlreadyUsedException.class);
        thrown.expectMessage("RFID token is already used!");

        customerService.add(customer);
    }

    @Test
    public void addNull() throws Exception {
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Customer can not be null!");

        customerService.add(null);
    }

    @Test
    public void addNameNull() throws Exception {
        Customer customer = new Customer( null, "email", "ch_username", "02030405");
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Name is empty, but a required field, so please fill in this field!");

        customerService.add(customer);
    }

    @Test
    public void addEmailNull() throws Exception {
        Customer customer = new Customer("name", null, "ch_username", "02030405");
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("Email is empty, but a required field, so please fill in this field!");

        customerService.add(customer);
    }

    @Test
    public void addRFIDTokenNull() throws Exception {
        Customer customer = new Customer("name", "email", "ch_username", null);
        thrown.expect(InvalidCustomerException.class);
        thrown.expectMessage("RFID token is empty, but a required field, so please fill in this field!");

        customerService.add(customer);
    }

    @Test
    public void update() throws Exception {
        testCustomer.setName("test-changed");
        customerService.update(testCustomer);

        Customer temp = customerService.getByKey(testCustomer.getKey());

        assertEquals("test-changed", temp.getName());
    }

    @Test
    public void delete() throws Exception {
        customerService.delete(testCustomer);

        assertEquals(0, customerService.getAllCustomers().size());
    }

    @Test
    public void deleteWithOrders() throws Exception {
        Order order = new Order();
        order.setCustomer(testCustomer);
        orderResporitory.save(order);

        thrown.expect(CustomerException.class);
        thrown.expectMessage("Customer has already placed orders, so it can not be deleted!");

        customerService.delete(testCustomer);
    }

}