package ch.wisv.events.core.service.product;

import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.SoldProductRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class)
@ActiveProfiles("test")
public class SoldProductServiceTest {

    /**
     * Mock of the SoldProductRepository
     */
    @Mock
    private SoldProductRepository repository;

    /**
     * SoldProductService with the mock of the repository
     */
    @InjectMocks
    private SoldProductService soldProductService = new SoldProductServiceImpl();

    /**
     * ExpectedException Object
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Default instance of the SoldProduct class
     */
    private SoldProduct soldProduct;

    /**
     * Method setUp create default SoldProduct class instance
     *
     * @throws Exception when
     */
    @Before
    public void setUp() throws Exception {
        soldProduct = new SoldProduct();

        soldProduct.setCustomer(Mockito.mock(Customer.class));
        soldProduct.setProduct(Mockito.mock(Product.class));
        soldProduct.setOrder(Mockito.mock(Order.class));
    }

    /**
     * Method tearDown set SoldProduct to null.
     *
     * @throws Exception when
     */
    @After
    public void tearDown() throws Exception {
        soldProduct = null;
    }

    /**
     * Method getAll ...
     *
     * @throws Exception when
     */
    @Test
    public void getAll() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(soldProduct));

        assertEquals(1, soldProductService.getAll().size());
    }

    /**
     * Method getByProduct ...
     *
     * @throws Exception when
     */
    @Test
    public void getByProduct() throws Exception {
        when(repository.findAllByProduct(Mockito.any(Product.class))).thenReturn(
                Collections.singletonList(soldProduct));

        List<SoldProduct> temp = soldProductService.getByProduct(Mockito.mock(Product.class));

        assertEquals(Collections.singletonList(soldProduct), temp);
    }

    /**
     * Method getByProductNull ...
     *
     * @throws Exception when
     */
    @Test
    public void getByProductNull() throws Exception {
        List<SoldProduct> temp = soldProductService.getByProduct(null);

        assertEquals(Collections.emptyList(), temp);
    }

    /**
     * Method getByProductNullProductDoesntExists ...
     *
     * @throws Exception when
     */
    @Test
    public void getByProductNullProductDoesntExists() throws Exception {
        List<SoldProduct> temp = soldProductService.getByProduct(new Product());

        assertEquals(Collections.emptyList(), temp);
    }

    /**
     * Method getByCustomerAndProduct ...
     *
     * @throws Exception when
     */
    @Test
    public void getByCustomerAndProduct() throws Exception {
        when(repository.findAllByCustomerAndProduct(Mockito.any(Customer.class), Mockito.any(Product.class)))
                .thenReturn(Collections.singletonList(soldProduct));

        List<SoldProduct> temp = soldProductService.getByCustomerAndProduct(Mockito.mock(Customer.class), Mockito
                .mock(Product.class));

        assertEquals(Collections.singletonList(soldProduct), temp);
    }

    /**
     * Method getByCustomerAndProductNull ...
     *
     * @throws Exception when
     */
    @Test
    public void getByCustomerAndProductNull() throws Exception {
        List<SoldProduct> temp = soldProductService.getByCustomerAndProduct(null, null);

        assertEquals(Collections.emptyList(), temp);
    }

    /**
     * Method getByCustomer ...
     *
     * @throws Exception when
     */
    @Test
    public void getByCustomer() throws Exception {
        when(repository.findAllByCustomer(Mockito.any(Customer.class)))
                .thenReturn(Collections.singletonList(soldProduct));
        List<SoldProduct> temp = soldProductService.getByCustomer(Mockito.mock(Customer.class));

        assertEquals(Collections.singletonList(soldProduct), temp);
    }

    /**
     * Method getByCustomerNull ...
     *
     * @throws Exception when
     */
    @Test
    public void getByCustomerNull() throws Exception {
        List<SoldProduct> temp = soldProductService.getByCustomer(null);

        assertEquals(Collections.emptyList(), temp);
    }

    @Test
    public void create() throws Exception {

    }

    @Test
    public void remove() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

}