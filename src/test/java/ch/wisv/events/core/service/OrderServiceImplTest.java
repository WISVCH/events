package ch.wisv.events.core.service;

import ch.wisv.events.app.request.OrderRequest;
import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.exception.ProductLimitExceededException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderServiceImpl;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.SoldProductService;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
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
public class OrderServiceImplTest extends ServiceTest {

    /**
     * Mock OrderRepository
     */
    @Mock
    private OrderRepository repository;

    /**
     * Mock EventService
     */
    @Mock
    private EventService eventService;

    /**
     * Mock ProductService
     */
    @Mock
    private ProductService productService;

    /**
     * Mock SoldProductService
     */
    @Mock
    private SoldProductService soldProductService;

    /**
     * Field service
     */
    private OrderService service;

    /**
     * Default Order
     */
    private Order order;

    /**
     * Method setUp ...
     *
     * @throws Exception when
     */
    @Before
    public void setUp() throws Exception {
        this.service = new OrderServiceImpl(repository, eventService, productService, soldProductService);

        this.order = new Order(mock(Customer.class));
    }

    /**
     * Method tearDown ...
     *
     * @throws Exception when
     */
    @After
    public void tearDown() throws Exception {
        this.order = null;
    }


    /**
     * Test get all product method
     *
     * @throws Exception when
     */
    @Test
    public void testGetAllProducts() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(this.order));

        assertEquals(Collections.singletonList(this.order), service.getAllOrders());
    }

    /**
     * Test get all product method
     *
     * @throws Exception when
     */
    @Test
    public void testGetAllProductsEmpty() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), service.getAllOrders());
    }

    /**
     * Test get by public reference method
     *
     * @throws Exception when
     */
    @Test
    public void testGetByReference() throws Exception {
        when(repository.findByPublicReference(this.order.getPublicReference())).thenReturn(Optional.of(this.order));

        assertEquals(this.order, service.getByReference(this.order.getPublicReference()));
    }

    /**
     * Test get by public reference when order does not exists
     *
     * @throws Exception when
     */
    @Test
    public void testGetByReferenceEmpty() throws Exception {
        thrown.expect(EventsModelNotFound.class);
        when(repository.findByPublicReference(this.order.getPublicReference())).thenReturn(Optional.empty());

        service.getByReference(this.order.getPublicReference());
    }

    /**
     * Method testGetOrdersByProduct ...
     *
     * @throws Exception when
     */
    @Test
    public void testGetOrdersByProduct() throws Exception {
        Product product = new Product();
        product.setCost(0.d);
        this.order.addProduct(product);

        when(repository.findAll()).thenReturn(ImmutableList.of(this.order, new Order()));

        assertEquals(ImmutableList.of(this.order), service.getOrdersByProduct(product));
    }

    /**
     * Method testGetOrdersByProduct ...
     *
     * @throws Exception when
     */
    @Test
    public void testGetOrdersByProductEmpty() throws Exception {
        Product product = new Product();
        product.setCost(0.d);
        this.order.addProduct(product);

        when(repository.findAll()).thenReturn(ImmutableList.of(new Order()));

        assertEquals(ImmutableList.of(), service.getOrdersByProduct(product));
    }

    /**
     * Method testCreate ...
     *
     * @throws Exception when
     */
    @Test
    public void testCreate() throws Exception {
        Product product = new Product("Test", "test", 1.d, 10, LocalDateTime.now(), LocalDateTime.now());
        HashMap<String, Integer> products = new HashMap<>();
        products.put(product.getKey(), 2);

        OrderRequest request = new OrderRequest(products);
        when(productService.getByKey(product.getKey())).thenReturn(product);

//        TODO: repair test
//        Order temp = service.create(request);
//        verify(repository, times(1)).saveAndFlush(any(Order.class));
//        assertEquals(ImmutableList.of(product, product), temp.getProducts());
    }


    /**
     * Method testCreate ...
     *
     * @throws Exception when
     */
    @Test
    public void testCreateException() throws Exception {
        Product product = new Product("Test", "test", 1.d, 1, LocalDateTime.now(), LocalDateTime.now());
        HashMap<String, Integer> products = new HashMap<>();
        products.put(product.getKey(), 2);

        OrderRequest request = new OrderRequest(products);
        when(productService.getByKey(product.getKey())).thenReturn(product);

        thrown.expect(ProductLimitExceededException.class);
        service.create(request);
    }

    /**
     * Method testAddCustomerToOrder ...
     *
     * @throws Exception when
     */
    @Test
    public void testAddCustomerToOrder() throws Exception {
        Customer customer = Mockito.mock(Customer.class);

        service.addCustomerToOrder(this.order, customer);
        verify(repository, times(1)).save(this.order);
    }

    /**
     * Method testUpdateOrderStatus ...
     *
     * @throws Exception when
     */
    @Test
    public void testUpdateOrderStatus() throws Exception {
        Event event = new Event("Test", "test", "test", 10, 10, "path/to/file", LocalDateTime.now(),
                LocalDateTime.now(), null);
        Product product = new Product("Test", "test", 1.d, 1, LocalDateTime.now(), LocalDateTime.now());
        this.order.addProduct(product);
        event.addProduct(product);

        when(eventService.getEventByProductKey(product.getKey())).thenReturn(ImmutableList.of(event));

        service.updateOrderStatus(this.order, OrderStatus.REJECTED);

        verify(repository, times(1)).save(this.order);
        verify(eventService, times(1)).update(any(Event.class));
    }

    /**
     * Method testUpdateOrderStatusToPaid ...
     *
     * @throws Exception when
     */
    @Test
    public void testUpdateOrderStatusToPaid() throws Exception {
        Product product = new Product("Test", "test", 1.d, 1, LocalDateTime.now(), LocalDateTime.now());
        this.order.addProduct(product);

        service.updateOrderStatus(this.order, OrderStatus.PAID_CASH);
        verify(soldProductService, times(1)).create(this.order);
    }

    /**
     * Method testUpdateOrderStatusToPaid ...
     *
     * @throws Exception when
     */
    @Test
    public void testUpdateOrderStatusToPaidDouble() throws Exception {
        Product product = new Product("Test", "test", 1.d, 1, LocalDateTime.now(), LocalDateTime.now());
        this.order.addProduct(product);
        this.order.setStatus(OrderStatus.PAID_CASH);

        service.updateOrderStatus(this.order, OrderStatus.PAID_CASH);
        verify(soldProductService, times(0)).create(this.order);
    }

    /**
     * Method testUpdateOrderStatusToPaid ...
     *
     * @throws Exception when
     */
    @Test
    public void testUpdateOrderStatusFromPaid() throws Exception {
        Product product = new Product("Test", "test", 1.d, 1, LocalDateTime.now(), LocalDateTime.now());
        this.order.addProduct(product);
        this.order.setStatus(OrderStatus.PAID_CASH);

        service.updateOrderStatus(this.order, OrderStatus.REJECTED);
        verify(soldProductService, times(1)).remove(this.order);
    }

    /**
     * Method testUpdateOrderStatusToPaid ...
     *
     * @throws Exception when
     */
    @Test
    public void testUpdateOrderStatusFromPaidDouble() throws Exception {
        Product product = new Product("Test", "test", 1.d, 1, LocalDateTime.now(), LocalDateTime.now());
        this.order.addProduct(product);
        this.order.setStatus(OrderStatus.REJECTED);

        service.updateOrderStatus(this.order, OrderStatus.REJECTED);
        verify(soldProductService, times(0)).remove(this.order);
    }

}