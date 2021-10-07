package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.runtime.CustomerAlreadyPlacedOrdersException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.customer.CustomerServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void setUp() {
        customerService = new CustomerServiceImpl(repository, orderRepository);

        this.customer = new Customer("", "Christiaan Huygens", "events@ch.tudelft.nl", "12345678");
    }

    /**
     * Test get all customers
     */
    @Test
    public void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(Collections.singletonList(this.customer));

        assertEquals(Collections.singletonList(this.customer), customerService.getAllCustomers());
    }

    /**
     * Test get all customers with an empty list
     */
    @Test
    public void testGetAllCustomersEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), customerService.getAllCustomers());
    }

    /**
     * Test get all customers with an empty list
     */
    @Test
    public void testGetAllCustomersCreatedAfter() {
        LocalDateTime time = LocalDateTime.now();
        when(repository.findAllByCreatedAtAfter(time)).thenReturn(Collections.singletonList(this.customer));

        assertEquals(Collections.singletonList(this.customer), customerService.getAllCustomerCreatedAfter(time));
    }

    /**
     * Test get by key method
     */
    @Test
    public void testGetByKey() throws Exception {
        when(repository.findByKey(this.customer.getKey())).thenReturn(Optional.of(this.customer));

        assertEquals(this.customer, customerService.getByKey(this.customer.getKey()));
    }

    /**
     * Test get by key not found
     */
    @Test
    public void testGetByKeyNotFound() throws Exception {
        thrown.expect(CustomerNotFoundException.class);
        thrown.expectMessage("Customer with key key not found!");
        when(repository.findByKey(anyString())).thenReturn(Optional.empty());

        customerService.getByKey("key");
    }

    /**
     * Test get by sub method
     */
    @Test
    public void testGetBySub() throws Exception {
        when(repository.findBySub(this.customer.getSub())).thenReturn(Optional.of(this.customer));

        assertEquals(this.customer, customerService.getBySub(this.customer.getSub()));
    }

    /**
     * Test get by sub not found
     */
    @Test
    public void testGetBySubNotFound() throws Exception {
        thrown.expect(CustomerNotFoundException.class);
        thrown.expectMessage("Customer with sub sub not found!");
        when(repository.findBySub(anyString())).thenReturn(Optional.empty());

        customerService.getBySub("sub");
    }

    /**
     * Test get by key method
     */
    @Test
    public void testGetByRFIDToken() throws Exception {
        when(repository.findByRfidToken(this.customer.getRfidToken())).thenReturn(Optional.of(this.customer));

        assertEquals(this.customer, customerService.getByRfidToken(this.customer.getRfidToken()));
    }

    /**
     * Test get by key not found
     */
    @Test
    public void testGetByRFIDTokenNotFound() throws Exception {
        thrown.expect(CustomerNotFoundException.class);
        thrown.expectMessage("Customer with rfid token 123 not found!");
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());

        customerService.getByRfidToken("123");
    }

    /**
     * Test create method and verify that saveAndFlush is called once
     */
    @Test
    public void testCreate() throws Exception {
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        customerService.create(this.customer);
        verify(repository, times(1)).saveAndFlush(any(Customer.class));
    }

    /**
     * Test create method and verify that saveAndFlush is called once
     */
    @Test
    public void testCreateNull() throws Exception {
        thrown.expect(CustomerInvalidException.class);
        thrown.expectMessage("Customer can not be null!");

        customerService.create(null);
    }

    /**
     * Test create method when name is null
     */
    @Test
    public void testCreateMissingName() throws Exception {
        thrown.expect(CustomerInvalidException.class);
        thrown.expectMessage("Name is empty, but a required field, so please fill in this field!");
        this.customer.setName(null);

        customerService.create(this.customer);
    }

    /**
     * Test create method when email is null
     */
    @Test
    public void testCreateMissingEmail() throws Exception {
        thrown.expect(CustomerInvalidException.class);
        thrown.expectMessage("Email is empty, but a required field, so please fill in this field!");
        this.customer.setEmail(null);

        customerService.create(this.customer);
    }

    /**
     * Test create method when email is null
     */
    @Test
    public void testCreateMissingCreatedAt() throws Exception {
        thrown.expect(CustomerInvalidException.class);
        thrown.expectMessage("Customer should contain a created at timestamp.");
        this.customer.setCreatedAt(null);

        customerService.create(this.customer);
    }

    /**
     * Test create method when RFID token is null
     */
    @Test
    public void testCreateAlreadyUsedRFIDToken() throws Exception {
        Customer duplicate = new Customer("", "Constantijn Huygens", "events@ch.tudelft.nl", this.customer.getRfidToken());
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.of(this.customer));
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        thrown.expect(CustomerInvalidException.class);
        thrown.expectMessage("RFID token is already used!");

        customerService.create(duplicate);
    }

    /**
     * Test create method when RFID token is null
     */
    @Test
    public void testCreateAlreadyUsedEmail() throws Exception {
        Customer duplicate = new Customer("", "Constantijn Huygens", this.customer.getEmail(), this.customer.getRfidToken() + "0");
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(this.customer));
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());

        thrown.expect(CustomerInvalidException.class);
        thrown.expectMessage("Email address is already used!");

        customerService.create(duplicate);
    }

    /**
     * Test update method
     */
    @Test
    public void testUpdate() throws Exception {
        Customer update = new Customer();
        update.setKey(this.customer.getKey());
        update.setRfidToken("123");
        update.setEmail("test@test.com");
        update.setName("test test");

        Customer mock = new Customer();
        when(repository.findByKey(this.customer.getKey())).thenReturn(Optional.of(mock));
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());

        customerService.update(update);
        verify(repository, times(1)).save(mock);

        assertEquals(update.getName(), mock.getName());
        assertEquals(update.getEmail(), mock.getEmail());
        assertEquals(update.getRfidToken(), mock.getRfidToken());
    }

    /**
     * Test if create by ChUserInfo
     */
    @Test
    public void testCreateByChUserInfo() throws Exception {
        when(repository.findByRfidToken(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("name", "name");
        claims.put("email", "email");
        claims.put("ldapUsername", "ldapUsername");
        OidcUserInfo userInfo = new OidcUserInfo(claims);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        OidcIdToken idToken = new OidcIdToken("", Instant.now(),
                Instant.now().plusSeconds(60), claims);

        DefaultOidcUser oidcUser = new DefaultOidcUser(authorities, idToken, userInfo);

        Customer customer = customerService.createByOidcUser(oidcUser);

        assertEquals("name", customer.getName());
        assertEquals("email", customer.getEmail());
    }

    /**
     * Test if create by ChUserInfo
     */
    @Test
    public void testCreateByChUserInfoAssertion() throws Exception {
        thrown.expect(CustomerInvalidException.class);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("name", null);
        claims.put("email", null);
        claims.put("ldapUsername", "ldapUsername");

        OidcUserInfo userInfo = new OidcUserInfo(claims);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        OidcIdToken idToken = new OidcIdToken("", Instant.now(),
                Instant.now(), claims);
        DefaultOidcUser oidcUser = new DefaultOidcUser(authorities, idToken, userInfo);

        customerService.createByOidcUser(oidcUser);
    }

    /**
     * Test delete method when customer did not place an order
     */
    @Test
    public void testDelete() {
        when(orderRepository.findAllByOwnerOrderByCreatedAt(this.customer)).thenReturn(Collections.emptyList());

        customerService.delete(this.customer);
        verify(repository, times(1)).delete(this.customer);
    }

    /**
     * Test delete when customer placed an order
     */
    @Test
    public void testDeleteCustomerPlacedOrders() {
        when(orderRepository.findAllByOwnerOrderByCreatedAt(this.customer)).thenReturn(Collections.singletonList(any(Order.class)));

        thrown.expect(CustomerAlreadyPlacedOrdersException.class);
        thrown.expectMessage("Customer has already placed orders, so it can not be deleted!");

        customerService.delete(this.customer);
    }
}