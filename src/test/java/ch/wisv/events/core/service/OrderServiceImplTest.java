package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.UnassignedOrderException;
import ch.wisv.events.core.exception.normal.UndefinedPaymentMethodOrderException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailServiceImpl;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderProductRepository orderProductRepository;

    @MockBean
    private ProductService productService;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private MailServiceImpl mailService;

    /** OrderService. */
    @Autowired
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
        this.product = mock(Product.class);

        this.order = new Order();
        this.order.setOwner(mock(Customer.class));
        this.order.setCreatedBy("events-online");
        this.order.setAmount(1.d);
        this.order.setPaymentMethod(PaymentMethod.CASH);
        this.order.setStatus(OrderStatus.PAID);
        this.order.updateOrderAmount();

        OrderProduct orderProduct = new OrderProduct(this.product, 1.d, 1L);
        this.order.setOrderProducts(
                Collections.singletonList(orderProduct)
        );
    }

    /**
     * Test tear down method.
     */
    @After
    public void tearDown() {
        this.order = null;
        this.product = null;
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllProducts() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(this.order));

        assertEquals(Collections.singletonList(this.order), orderService.getAllOrders());
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllProductsEmpty() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), orderService.getAllOrders());
    }

    /**
     * Test get by public reference method
     *
     * @throws Exception when
     */
    @Test
    public void testGetByReference() throws Exception {
        when(orderRepository.findOneByPublicReference(this.order.getPublicReference())).thenReturn(Optional.of(this.order));

        assertEquals(this.order, orderService.getByReference(this.order.getPublicReference()));
    }

    /**
     * Test get by public reference when order does not exists
     *
     * @throws Exception when
     */
    @Test
    public void testGetByReferenceEmpty() throws Exception {
        thrown.expect(OrderNotFoundException.class);
        when(orderRepository.findOneByPublicReference(this.order.getPublicReference())).thenReturn(Optional.empty());

        orderService.getByReference(this.order.getPublicReference());
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllReservationOrderByCustomer() {
        when(orderRepository.findAllByOwnerAndStatus(any(Customer.class), eq(OrderStatus.RESERVATION))).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), orderService.getAllReservationOrderByCustomer(mock(Customer.class)));
    }

    @Test
    public void testCreate() throws Exception {
        orderService.create(this.order);

        verify(orderProductRepository, times(1)).saveAndFlush(this.order.getOrderProducts().get(0));
        verify(orderRepository, times(1)).saveAndFlush(this.order);
    }

    @Test
    public void testCreateNoProducts() throws Exception {
        thrown.expect(OrderInvalidException.class);
        this.order.setOrderProducts(null);

        orderService.create(this.order);
    }

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

        assertEquals(order.getOrderProducts(), Collections.singletonList(new OrderProduct(mockProduct, 1.d, 1L)));
    }

    @Test
    public void testUpdate() throws Exception {
        Order mock = mock(Order.class);

        when(orderRepository.findOneByPublicReference(this.order.getPublicReference())).thenReturn(Optional.of(mock));
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

        verify(orderRepository, times(1)).saveAndFlush(this.order);
    }

    @Test
    public void testUpdateNewOrder() throws Exception {
        thrown.expect(OrderNotFoundException.class);
        when(orderRepository.findOneByPublicReference(any(String.class))).thenReturn(Optional.empty());

        orderService.update(new Order());
    }

    @Test
    public void testUpdateMissingPublicReference() throws Exception {
        this.order.setPublicReference(null);
        thrown.expect(OrderInvalidException.class);
        when(orderRepository.findOneByPublicReference(any(String.class))).thenReturn(Optional.empty());

        orderService.update(this.order);
    }

    @Test
    public void testUpdateOrderStatus() throws Exception {
        Order mock = mock(Order.class);
        when(mock.getStatus()).thenReturn(OrderStatus.PENDING);
        orderService.updateOrderStatus(mock, OrderStatus.EXPIRED);

        verify(mock, times(1)).setStatus(OrderStatus.EXPIRED);
        verify(orderRepository, times(1)).saveAndFlush(mock);
    }

    @Test
    public void testUpdateOrderStatusPaid() throws Exception {
        Order mock = mock(Order.class);
        when(mock.getStatus()).thenReturn(OrderStatus.PENDING).thenReturn(OrderStatus.PAID);
        when(mock.getOwner()).thenReturn(mock(Customer.class));
        when(mock.getPaymentMethod()).thenReturn(PaymentMethod.IDEAL);
        when(mock.getOrderProducts()).thenReturn(this.order.getOrderProducts());
        orderService.updateOrderStatus(mock, OrderStatus.PAID);

        when(ticketService.createByOrderProduct(any(Order.class), any(OrderProduct.class))).thenReturn(mock(Ticket.class));
        doNothing().when(mailService).sendOrderConfirmation(mock, Collections.emptyList());

        verify(mock, times(1)).setStatus(OrderStatus.PAID);
        verify(mock, times(1)).setPaidAt(any(LocalDateTime.class));
        verify(ticketService, atLeastOnce()).createByOrderProduct(any(Order.class), any(OrderProduct.class));
        verify(productService, atLeastOnce()).update(any(Product.class));
    }

    @Test
    public void testUpdateOrderStatusPaidButWasAlreadyPaid() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Not allowed to update status from PAID to PAID");

        Order order = new Order();
        order.setStatus(OrderStatus.PAID);

        orderService.updateOrderStatus(order, OrderStatus.PAID);
    }

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
        Order mock = mock(Order.class);
        when(mock.getStatus()).thenReturn(OrderStatus.ASSIGNED);
        when(mock.getOwner()).thenReturn(mock(Customer.class));
        when(mock.getOrderProducts()).thenReturn(this.order.getOrderProducts());

        orderService.updateOrderStatus(mock, OrderStatus.RESERVATION);

        doNothing().when(mailService).sendOrderConfirmation(mock, Collections.emptyList());

        verify(mock, times(1)).setStatus(OrderStatus.RESERVATION);
        verify(productService, atLeastOnce()).update(any(Product.class));
        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
    }
}