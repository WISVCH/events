package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.runtime.OrderCannotUpdateException;
import ch.wisv.events.core.model.order.*;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderServiceImpl;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.SoldProductService;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
     * Mock OrderProductRepository
     */
    @Mock
    private OrderProductRepository orderProductRepository;

    /**
     * Mock SoldProductService
     */
    @Mock
    private SoldProductService soldProductService;

    /**
     * Field mailService
     */
    @Mock
    private MailService mailService;

    /**
     * Field mailService
     */
    @Mock
    private ProductService productService;

    /**
     * Field service
     */
    private OrderService service;

    /**
     * Default Order
     */
    private Order order;

    /**
     * Field product.
     */
    private Product product;

    /**
     * Method setUp ...
     *
     * @throws Exception when
     */
    @Before
    public void setUp() throws Exception {
        this.service = new OrderServiceImpl(repository, orderProductRepository, mailService, soldProductService, productService);

        this.product = mock(Product.class);

        this.order = new Order();
        this.order.setCustomer(mock(Customer.class));
        this.order.setCreatedBy("events-online");
        this.order.setAmount(1.d);

        OrderProduct orderProduct = new OrderProduct(this.product, 1.d, 1L);
        this.order.setOrderProducts(
                Collections.singletonList(orderProduct)
        );
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
        thrown.expect(OrderNotFoundException.class);
        when(repository.findByPublicReference(this.order.getPublicReference())).thenReturn(Optional.empty());

        service.getByReference(this.order.getPublicReference());
    }

    @Test
    public void testCreate() throws Exception {
        when(this.product.getMaxSold()).thenReturn(25);

        service.create(this.order);

        verify(orderProductRepository, times(1)).saveAndFlush(this.order.getOrderProducts().get(0));
        verify(repository, times(1)).saveAndFlush(this.order);
    }

    @Test
    public void testCreateNotEnoughTickets() throws Exception {
        thrown.expect(OrderInvalidException.class);

        when(soldProductService.getByProduct(this.product)).thenReturn(ImmutableList.of(new SoldProduct(), new SoldProduct()));
        when(this.product.getMaxSold()).thenReturn(2);
        when(this.product.getTitle()).thenReturn("Title");

        service.create(this.order);
    }

    @Test
    public void testUpdate() throws Exception {
        Order order = new Order(
                1,
                OrderStatus.OPEN,
                1.d,
                this.order.getOrderProducts(),
                this.order.getPublicReference(),
                this.order.getCreatedBy(),
                this.order.getCreationDate(),
                this.order.getPaidDate(),
                this.order.getCustomer()
        );
        service.update(order);
        verify(repository, times(1)).saveAndFlush(order);
    }

    @Test
    public void testUpdateNewOrder() throws Exception {
        thrown.expect(OrderCannotUpdateException.class);
        thrown.expectMessage("This object is new so can not be updated");

        service.update(new Order());
    }

    @Test
    public void testAssertIsValid() throws Exception {
        this.order.setAmount(1.d);
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidAmountNull() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order amount can not be null");

        this.order.setAmount(null);
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidAmountNegative() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order amount can not be negative");

        this.order.setAmount(-1.d);
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidNoProducts() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("OrderProducts list can not be null");

        this.order.setOrderProducts(Collections.emptyList());
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidProductsNull() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("OrderProducts list can not be null");

        this.order.setOrderProducts(null);
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidCreationDateNull() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order creation date can not be null");

        this.order.setCreationDate(null);
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidCreatedByNull() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order created by can not be null");

        this.order.setCreatedBy(null);
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidCreatedByEmpty() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order created by can not be null");

        this.order.setCreatedBy("");
        service.assertIsValid(this.order);
    }

    @Test
    public void testAssertIsValidForCustomer() throws Exception {
        Customer customer = mock(Customer.class);
        this.order.setCustomer(customer);
        when(this.product.getMaxSoldPerCustomer()).thenReturn(1);
        when(soldProductService.getAllByCustomerAndProduct(customer, this.product)).thenReturn(Collections.emptyList());

        service.assertIsValidForCustomer(this.order);
    }

    @Test
    public void testAssertIsNotValidForCustomer() throws Exception {
        thrown.expect(OrderInvalidException.class);

        Customer customer = mock(Customer.class);
        this.order.setCustomer(customer);

        when(this.product.getMaxSoldPerCustomer()).thenReturn(1);
        when(soldProductService.getAllByCustomerAndProduct(customer, this.product)).thenReturn(Collections.singletonList(new SoldProduct()));

        service.assertIsValidForCustomer(this.order);
    }

    @Test
    public void testCreateOrderByOrderProductDTO() throws Exception {
        when(productService.getByKey("key")).thenReturn(this.product);
        when(this.product.getCost()).thenReturn(1.d);

        HashMap<String, Long> products = new HashMap<>();
        products.put("key", 1L);
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProducts(products);

        Order order = service.createOrderByOrderProductDTO(orderProductDTO);

        assertEquals(1, order.getOrderProducts().size());
    }
}