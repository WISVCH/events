package ch.wisv.events.core.service;

import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderServiceImpl;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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
     * Method setUp ...
     *
     * @throws Exception when
     */
    @Before
    public void setUp() throws Exception {
        this.service = new OrderServiceImpl(repository, orderProductRepository, mailService, soldProductService, productService);

        this.order = new Order();
        this.order.setCustomer(mock(Customer.class));
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
}