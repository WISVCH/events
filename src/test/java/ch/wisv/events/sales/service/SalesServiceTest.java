package ch.wisv.events.sales.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.*;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.utils.LdapGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SalesServiceTest extends ServiceTest {

    /**
     * EventService.
     */
    @Mock
    private EventService eventService;

    /**
     * OrderService.
     */
    @Mock
    private OrderService orderService;

    /**
     * SalesService.
     */
    private SalesService salesService;

    /**
     * Order.
     */
    private Order order;

    /**
     * Event
     */
    private Event event;

    /**
     * Product.
     */
    private Product product;

    /**
     * Test set up method.
     */
    @Before
    public void setUp() {
        salesService = new SalesServiceImpl(eventService, orderService);
        product = mock(Product.class);
        event = mock(Event.class);
        order = mock(Order.class);
    }

    /**
     * Test tear down method.
     */
    @After
    public void tearDown() {
        salesService = null;
        event = null;
        product = null;
        order = null;
    }

    /**
     * Test the get all granted events by customer method with a customer in the LDAP group bestuur
     */
    @Test
    public void testGetAllGrantedEventByCustomerAsBestuur() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(LdapGroup.BESTUUR);

        when(eventService.getUpcoming()).thenReturn(events);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        List<Event> returnedEvents = salesService.getAllGrantedEventByCustomer(customer);

        verify(eventService, times(1)).getUpcoming();

        assertEquals(events, returnedEvents);
    }

    /**
     * Test get all granted events by customer as customer without valid events
     */
    @Test
    public void testGetAllGrantedEventByInvalidCustomer() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(LdapGroup.WIFI);

        when(eventService.getUpcoming()).thenReturn(events);
        when(event.getOrganizedBy()).thenReturn(LdapGroup.BT);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        List<Event> returnedEvents = salesService.getAllGrantedEventByCustomer(customer);

        verify(eventService, times(1)).getUpcoming();

        assertEquals(0, returnedEvents.size());
    }

    /**
     * Test get all granted events by customer as customer without valid events
     */
    @Test
    public void testGetAllGrantedEventByValidCustomer() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(LdapGroup.WIFI);

        when(eventService.getUpcoming()).thenReturn(events);
        when(event.getOrganizedBy()).thenReturn(LdapGroup.WIFI);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        List<Event> returnedEvents = salesService.getAllGrantedEventByCustomer(customer);

        verify(eventService, times(1)).getUpcoming();

        assertEquals(events, returnedEvents);
    }

    /**
     * Test get all granted events by customer as customer without valid events
     */
    @Test
    public void testGetAllGrantedProductByValidCustomer() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        List<Product> products = new ArrayList<>();
        products.add(product);

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(LdapGroup.WIFI);

        when(eventService.getUpcoming()).thenReturn(events);
        when(event.getOrganizedBy()).thenReturn(LdapGroup.WIFI);
        when(event.getProducts()).thenReturn(products);
        when(product.getSellStart()).thenReturn(LocalDateTime.MIN);
        when(product.getSellEnd()).thenReturn(LocalDateTime.MAX);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        List<Product> returnedProducts = salesService.getAllGrantedProductByCustomer(customer);

        verify(eventService, times(1)).getUpcoming();

        assertEquals(products, returnedProducts);
    }


    /**
     * Test get all granted events by customer as customer without valid events
     */
    @Test
    public void testGetAllOrdersByEvent() throws EventNotFoundException {
        List<Event> events = new ArrayList<>();
        events.add(event);

        List<Product> products = new ArrayList<>();
        products.add(product);

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        when(event.getProducts()).thenReturn(products);
        when(orderService.getAllByProduct(product)).thenReturn(orders);

        List<Order> returnedOrders = salesService.getAllOrdersByEvent(event);

        assertEquals(orders, returnedOrders);
    }

}