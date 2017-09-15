package ch.wisv.events.core.service;

import ch.wisv.events.core.exception.SoldProductNotFoundException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.SoldProductRepository;
import ch.wisv.events.core.service.product.SoldProductService;
import ch.wisv.events.core.service.product.SoldProductServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
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
public class SoldProductServiceTest extends ServiceTest {

    /**
     * Mock of the SoldProductRepository
     */
    @Mock
    private SoldProductRepository repository;

    /**
     * SoldProductService with the mock of the repository
     */
    private SoldProductService soldProductService;

    /**
     * Default instance of the SoldProduct class
     */
    private SoldProduct soldProduct;

    /**
     * Method setUp
     */
    @Before
    public void setUp() throws Exception {
        this.soldProductService = new SoldProductServiceImpl(repository);
        soldProduct = new SoldProduct();

        soldProduct.setCustomer(mock(Customer.class));
        soldProduct.setProduct(mock(Product.class));
        soldProduct.setOrder(mock(Order.class));
    }

    /**
     * Method tearDown
     */
    @After
    public void tearDown() throws Exception {
        soldProduct = null;
    }

    /**
     * Test get by key
     */
    @Test
    public void testGetByKey() {
        when(repository.findByKey(this.soldProduct.getKey())).thenReturn(Optional.of(this.soldProduct));

        assertEquals(this.soldProduct, soldProductService.getByKey(this.soldProduct.getKey()));
    }

    /**
     * Test get by key
     */
    @Test
    public void testGetByKeyEmpty() {
        when(repository.findByKey(this.soldProduct.getKey())).thenReturn(Optional.empty());

        thrown.expect(SoldProductNotFoundException.class);
        thrown.expectMessage("SoldProduct with key " + this.soldProduct.getKey() + " is not exists!");
        soldProductService.getByKey(this.soldProduct.getKey());
    }

    /**
     * Test get all method with response
     */
    @Test
    public void getAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(soldProduct));

        assertEquals(Collections.singletonList(soldProduct), soldProductService.getAll());
    }


    /**
     * Test get all method with response
     */
    @Test
    public void getAllEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), soldProductService.getAll());
    }


    /**
     * Test get sold product by product
     */
    @Test
    public void getByProduct() {
        when(repository.findAllByProduct(any(Product.class))).thenReturn(
                Collections.singletonList(soldProduct));

        List<SoldProduct> temp = soldProductService.getByProduct(mock(Product.class));

        assertEquals(Collections.singletonList(soldProduct), temp);
    }

    /**
     * Method get sold product by when product is not there
     */
    @Test
    public void getByProductNull() {
        when(repository.findAllByProduct(any(Product.class))).thenReturn(Collections.emptyList());
        List<SoldProduct> temp = soldProductService.getByProduct(null);

        assertEquals(Collections.emptyList(), temp);
    }

    /**
     * Test get by customer and product
     */
    @Test
    public void getByCustomerAndProduct() {
        when(repository.findAllByCustomerAndProduct(any(Customer.class), any(Product.class)))
                .thenReturn(Collections.singletonList(soldProduct));

        List<SoldProduct> temp = soldProductService.getAllByCustomerAndProduct(mock(Customer.class),
                mock(Product.class));

        assertEquals(Collections.singletonList(soldProduct), temp);
    }

    /**
     * Test get by customer and product null
     */
    @Test
    public void getByCustomerAndProductNull() {
        List<SoldProduct> temp = soldProductService.getAllByCustomerAndProduct(null, null);

        assertEquals(Collections.emptyList(), temp);
    }

    /**
     * Test get by customer
     */
    @Test
    public void getByCustomer() {
        when(repository.findAllByCustomer(any(Customer.class)))
                .thenReturn(Collections.singletonList(soldProduct));

        List<SoldProduct> temp = soldProductService.getByCustomer(mock(Customer.class));

        assertEquals(Collections.singletonList(soldProduct), temp);
    }

    /**
     * Test get by customer when customer is null
     */
    @Test
    public void getByCustomerNull() {
        when(repository.findAllByCustomer(this.soldProduct.getCustomer())).thenReturn(Collections.emptyList());
        List<SoldProduct> temp = soldProductService.getByCustomer(null);

        assertEquals(Collections.emptyList(), temp);
    }

    /**
     * Test create method
     */
    @Test
    public void create() {
        Order order = new Order();

        Product product1 = new Product();
        product1.setCost(0.d);
        Product product2 = new Product();
        product2.setCost(0.d);

        order.addProduct(product1);
        order.addProduct(product2);

        soldProductService.create(order);

        verify(repository, times(2)).saveAndFlush(any(SoldProduct.class));
    }

    /**
     * Test create method when order has no products
     */
    @Test
    public void createOrderNotProducts() {
        Order order = new Order();

        soldProductService.create(order);
        verify(repository, times(0)).saveAndFlush(any(SoldProduct.class));
    }

    /**
     * Test delete sold products from an order
     */
    @Test
    public void remove() {
        when(repository.findAllByOrder(any(Order.class))).thenReturn(Collections.singletonList(this.soldProduct));

        soldProductService.delete(any(Order.class));
        verify(repository, times(1)).delete(Collections.singletonList(this.soldProduct));
    }

    /**
     * Test update method
     */
    @Test
    public void update() {
        when(repository.findByKey(this.soldProduct.getKey())).thenReturn(Optional.of(this.soldProduct));
        soldProductService.update(this.soldProduct);

        verify(repository, times(1)).save(this.soldProduct);
    }

}