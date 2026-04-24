package ch.wisv.events.sales.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.*;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.utils.LdapGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SalesServiceTest extends ServiceTest {

    /**
     * EventService.
     */
    @Mock
    private EventRepository eventRepository;

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
        salesService = new SalesServiceImpl(eventRepository, orderService);
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
        SecurityContextHolder.clearContext();
    }

    /**
     * Test the get all granted events by customer method with a customer in the LDAP group bestuur
     */
    @Test
    public void testGetAllGrantedEventByCustomerAsAdminRole() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        List<LdapGroup> ldapGroups = new ArrayList<>();
        ldapGroups.add(LdapGroup.BESTUUR);

        when(eventRepository.findAllSalesVisibleEvents(any(LocalDateTime.class), anyCollection())).thenReturn(events);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "admin",
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));

        List<Event> returnedEvents = salesService.getAllGrantedEventByCustomer(customer);

        verify(eventRepository, times(1)).findAllSalesVisibleEvents(any(LocalDateTime.class), anyCollection());
        verify(eventRepository, times(0)).findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection());

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

        when(eventRepository.findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection()))
                .thenReturn(new ArrayList<>());

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "user",
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        ));

        List<Event> returnedEvents = salesService.getAllGrantedEventByCustomer(customer);

        verify(eventRepository, times(1)).findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection());

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

        when(eventRepository.findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection()))
                .thenReturn(events);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "user",
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        ));

        List<Event> returnedEvents = salesService.getAllGrantedEventByCustomer(customer);

        verify(eventRepository, times(1)).findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection());

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

        when(eventRepository.findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection()))
                .thenReturn(events);
        when(event.getProducts()).thenReturn(products);
        when(product.getSellStart()).thenReturn(LocalDateTime.MIN);
        when(product.getSellEnd()).thenReturn(LocalDateTime.MAX);

        Customer customer = mock(Customer.class);
        when(customer.getLdapGroups()).thenReturn(ldapGroups);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "user",
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        ));

        List<Product> returnedProducts = salesService.getAllGrantedProductByCustomer(customer);

        verify(eventRepository, times(1)).findAllSalesVisibleEventsByOrganizedByIn(any(LocalDateTime.class), anyCollection(), anyCollection());

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