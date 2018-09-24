package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.LogLevelEnum;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.runtime.EventsRuntimeException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderServiceImpl;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    /** OrderRepository. */
    @Mock
    private OrderRepository orderRepository;

    /** OrderProductRepository. */
    @Mock
    private OrderProductRepository orderProductRepository;

    /** OrderValidationService. */
    @Mock
    private OrderValidationService orderValidationService;

    /** ProductService. */
    @Mock
    private ProductService productService;

    /** MailService. */
    @Mock
    private MailService mailService;

    /** TicketService. */
    @Mock
    private TicketService ticketService;

    /** OrderService. */
    private OrderService orderService;

    /** Order. */
    private Order order;

    /** Product. */
    private Product product;

    /**
     * Test set up method.
     */
    @Before
    public void setUp() {
        orderService = new OrderServiceImpl(
                orderRepository,
                orderProductRepository,
                orderValidationService,
                productService,
                mailService,
                ticketService
        );
        product = mock(Product.class);

        order = new Order();
        order.setOwner(mock(Customer.class));
        order.setCreatedBy("events-online");
        order.setAmount(1.d);
        order.setPaymentMethod(PaymentMethod.CASH);
        order.setStatus(OrderStatus.PAID);
        order.updateOrderAmount();

        OrderProduct orderProduct = new OrderProduct(product, 1.d, 1L);
        order.setOrderProducts(
                ImmutableList.of(orderProduct)
        );
    }

    /**
     * Test tear down method.
     */
    @After
    public void tearDown() {
        orderService = null;
        order = null;
        product = null;
    }

    /**
     * Test add Customer to Order.
     */
    @Test
    public void testAddCustomerToOrder() throws EventsException {
        order.setStatus(OrderStatus.ANONYMOUS);
        doNothing().when(orderValidationService).assertOrderIsValidForCustomer(order, order.getOwner());
        when(orderRepository.saveAndFlush(order)).thenReturn(order);
        when(orderRepository.findOneByPublicReference(order.getPublicReference())).thenReturn(Optional.of(order));
        orderService.addCustomerToOrder(order, order.getOwner());

        verify(orderValidationService, times(1)).assertOrderIsValidForCustomer(order, order.getOwner());
        verify(orderRepository, times(2)).saveAndFlush(order);

        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
    }

    /**
     * Test add Customer to Order when OrderStatus is not ANONYMOUS.
     */
    @Test
    public void testAddCustomerToOrderInvalidStatus() throws EventsException {
        order.setStatus(OrderStatus.ASSIGNED);

        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("This is not possible to add a Customer to an Order with status " + order.getStatus());

        orderService.addCustomerToOrder(order, order.getOwner());
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllProducts() {
        when(orderRepository.findAll()).thenReturn(ImmutableList.of(order));

        assertEquals(ImmutableList.of(order), orderService.getAllOrders());
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllProductsEmpty() {
        when(orderRepository.findAll()).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), orderService.getAllOrders());
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetReservationByCustomer() {
        when(orderRepository.findAllByOwnerAndStatusOrderByCreatedAt(order.getOwner(), OrderStatus.RESERVATION))
                .thenReturn(ImmutableList.of(order));

        assertEquals(ImmutableList.of(order), orderService.getReservationByOwner(order.getOwner()));
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetReservationByCustomerEmpty() {
        when(orderRepository.findAllByOwnerAndStatusOrderByCreatedAt(order.getOwner(), OrderStatus.RESERVATION)).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), orderService.getReservationByOwner(order.getOwner()));
    }

    /**
     * Test get by public reference method
     *
     * @throws Exception when
     */
    @Test
    public void testGetByReference() throws Exception {
        when(orderRepository.findOneByPublicReference(order.getPublicReference())).thenReturn(Optional.of(order));

        assertEquals(order, orderService.getByReference(order.getPublicReference()));
    }

    /**
     * Test get by public reference when order does not exists
     *
     * @throws Exception when
     */
    @Test
    public void testGetByReferenceEmpty() throws Exception {
        thrown.expect(OrderNotFoundException.class);
        when(orderRepository.findOneByPublicReference(order.getPublicReference())).thenReturn(Optional.empty());

        orderService.getByReference(order.getPublicReference());
    }

    /**
     * Create test.
     */
    @Test
    public void testCreate() throws Exception {
        orderService.create(order);

        verify(orderProductRepository, times(1)).saveAndFlush(order.getOrderProducts().get(0));
        verify(orderRepository, times(1)).saveAndFlush(order);
    }

    /**
     * Create test with no products.
     */
    @Test
    public void testCreateNoProducts() throws Exception {
        thrown.expect(OrderInvalidException.class);
        order.setOrderProducts(null);

        orderService.create(order);
    }

    /**
     * Create test using OrderProductDto.
     */
    @Test
    public void testCreateByOrderProductDto() throws Exception {
        HashMap<String, Long> products = new HashMap<>();
        products.put("123-345-456", 1L);
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setProducts(products);

        Product mockProduct = mock(Product.class);
        when(mockProduct.getCost()).thenReturn(1.d);
        when(productService.getByKey("123-345-456")).thenReturn(mockProduct);

        Order order = orderService.createOrderByOrderProductDto(orderProductDto);

        assertEquals(order.getOrderProducts(), ImmutableList.of(new OrderProduct(mockProduct, 1.d, 1L)));
    }

    /**
     * Update test.
     */
    @Test
    public void testUpdate() throws Exception {
        Order mock = mock(Order.class);

        when(orderRepository.findOneByPublicReference(order.getPublicReference())).thenReturn(Optional.of(mock));
        orderService.update(order);

        // Updates that should happen
        verify(mock, times(1)).setOwner(any(Customer.class));
        verify(mock, times(1)).setPaymentMethod(any(PaymentMethod.class));
        verify(mock, times(1)).updateOrderAmount();

        // Updates that should not happen
        verify(mock, times(0)).setPublicReference(any(String.class));
        verify(mock, times(0)).setOrderProducts(any(List.class));
        verify(mock, times(0)).setStatus(any(OrderStatus.class));
        verify(mock, times(0)).setCreatedBy(any(String.class));
        verify(mock, times(0)).setCreatedAt(any(LocalDateTime.class));
        verify(mock, times(0)).setPaidAt(any(LocalDateTime.class));

        verify(orderRepository, times(1)).saveAndFlush(order);
    }

    /**
     * Update test with new Order.
     */
    @Test
    public void testUpdateNewOrder() throws Exception {
        thrown.expect(OrderNotFoundException.class);
        when(orderRepository.findOneByPublicReference(any(String.class))).thenReturn(Optional.empty());

        orderService.update(new Order());
    }

    /**
     * Update test missing public reference.
     */
    @Test
    public void testUpdateMissingPublicReference() throws Exception {
        order.setPublicReference(null);
        thrown.expect(OrderInvalidException.class);
        when(orderRepository.findOneByPublicReference(any(String.class))).thenReturn(Optional.empty());

        orderService.update(order);
    }

    /**
     * Update OrderStatus.
     */
    @Test
    public void testUpdateOrderStatus() throws Exception {
        Order mock = mock(Order.class);
        when(mock.getStatus()).thenReturn(OrderStatus.PENDING);
        orderService.updateOrderStatus(mock, OrderStatus.EXPIRED);

        verify(mock, times(1)).setStatus(OrderStatus.EXPIRED);
        verify(orderRepository, times(1)).saveAndFlush(mock);
    }

    /**
     * Update OrderStatus.
     */
    @Test
    public void testUpdateOrderStatusPaid() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(ticketService.createByOrder(order)).thenReturn(new ArrayList<>());
        orderService.updateOrderStatus(order, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertNotEquals(null, order.getPaidAt());
        verify(ticketService, times(1)).createByOrder(order);
    }

    /**
     * Update OrderStatus.
     */
    @Test
    public void testUpdateOrderStatusPaidButWasAlreadyPaid() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Not allowed to update status from PAID to PAID");

        Order order = new Order();
        order.setStatus(OrderStatus.PAID);

        orderService.updateOrderStatus(order, OrderStatus.PAID);
    }

    /**
     * Update OrderStatus.
     */
    @Test
    public void testUpdateOrderStatusPaidMissingOwner() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Not allowed to update status from ANONYMOUS to PAID");

        Order mock = mock(Order.class);
        when(mock.getStatus()).thenReturn(OrderStatus.ANONYMOUS);

        orderService.updateOrderStatus(mock, OrderStatus.PAID);
    }

    @Test
    public void testUpdateOrderStatusReservation() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.ASSIGNED);

        when(ticketService.createByOrder(order)).thenReturn(ImmutableList.of());
        orderService.updateOrderStatus(order, OrderStatus.RESERVATION);

        assertEquals(OrderStatus.RESERVATION, order.getStatus());
    }

    @Test
    public void testUpdateOrderStatusRejectedFromPaid() throws Exception {
        Product product2 = new Product();
        product2.setSold(2);
        OrderProduct orderProduct = new OrderProduct(product2, 1.d, 1L);
        Order order = new Order();
        order.setStatus(OrderStatus.PAID);
        order.setOrderProducts(ImmutableList.of(orderProduct));

        doNothing().when(ticketService).deleteByOrder(order);
        orderService.updateOrderStatus(order, OrderStatus.REJECTED);

        assertEquals(OrderStatus.REJECTED, order.getStatus());
        verify(ticketService, times(1)).deleteByOrder(order);
    }

    @Test
    public void testUpdateOrderStatusRejectedFromReservation() throws Exception {
        Product product2 = new Product();
        product2.setReserved(2);
        OrderProduct orderProduct = new OrderProduct(product2, 1.d, 1L);
        Order order = new Order();
        order.setStatus(OrderStatus.RESERVATION);
        order.setOrderProducts(ImmutableList.of(orderProduct));

        doNothing().when(ticketService).deleteByOrder(order);
        orderService.updateOrderStatus(order, OrderStatus.REJECTED);

        assertEquals(OrderStatus.REJECTED, order.getStatus());
        verify(orderRepository, times(2)).saveAndFlush(order);
    }

    @Test
    public void testUpdateOrderStatusThreadSafety() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        when(ticketService.createByOrder(order)).thenReturn(new ArrayList<>());

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> assertEquals("Not allowed to update status from PAID to PAID", e.getMessage()));

        Thread t1 = new Thread(() -> {
            try {
                orderService.updateOrderStatus(order, OrderStatus.PAID);
            } catch (EventsException e) {
                throw new EventsRuntimeException(LogLevelEnum.DEBUG, e.getMessage());
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                orderService.updateOrderStatus(order, OrderStatus.PAID);
            } catch (EventsException e) {
                throw new EventsRuntimeException(LogLevelEnum.DEBUG, e.getMessage());
            }
        });
        t2.start();
    }

    /**
     * Test containsChOnlyProduct when is true.
     */
    @Test
    public void testContainsChOnlyProductTrue() {
        Product product2 = new Product();
        product2.setChOnly(true);
        product2.setCost(1.d);

        when(product.isChOnly()).thenReturn(false);
        OrderProduct orderProduct = new OrderProduct(product2, 1.d, 1L);
        Order order = new Order();
        order.addOrderProduct(orderProduct);

        assertTrue(orderService.containsChOnlyProduct(order));
    }

    /**
     * Test containsChOnlyProduct when is false.
     */
    @Test
    public void testContainsChOnlyProductFalse() {
        Product product2 = new Product();
        product2.setChOnly(false);
        product2.setCost(1.d);

        when(product.isChOnly()).thenReturn(false);
        OrderProduct orderProduct = new OrderProduct(product2, 1.d, 1L);
        Order order = new Order();
        order.addOrderProduct(orderProduct);

        assertFalse(orderService.containsChOnlyProduct(order));
    }

    /**
     * Test containsRegistrationProduct when is true.
     */
    @Test
    public void testContainsRegistrationProductTrue() {
        Product product2 = new Product();
        product2.setIncludesRegistration(true);
        product2.setCost(1.d);

        when(product.isIncludesRegistration()).thenReturn(false);
        OrderProduct orderProduct = new OrderProduct(product2, 1.d, 1L);
        Order order = new Order();
        order.addOrderProduct(orderProduct);

        assertTrue(orderService.containsRegistrationProduct(order));
    }

    /**
     * Test containsRegistrationProduct when is false.
     */
    @Test
    public void testContainsRegistrationProductFalse() {
        Product product2 = new Product();
        product2.setIncludesRegistration(false);
        product2.setCost(1.d);

        when(product.isIncludesRegistration()).thenReturn(false);
        OrderProduct orderProduct = new OrderProduct(product2, 1.d, 1L);
        Order order = new Order();
        order.addOrderProduct(orderProduct);

        assertFalse(orderService.containsRegistrationProduct(order));
    }

    /**
     * Test containsRegistrationProduct when is false.
     */
    @Test
    public void testDelete() {
        doNothing().when(orderRepository).delete(order);

        orderService.delete(order);

        verify(orderRepository, times(1)).delete(order);
    }
}