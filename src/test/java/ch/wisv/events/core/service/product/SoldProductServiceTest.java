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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
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
@DataJpaTest
public class SoldProductServiceTest {

    @Mock
    private SoldProductRepository repository;

    @InjectMocks
    private SoldProductService soldProductService = new SoldProductServiceImpl();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SoldProduct soldProduct;

    @Before
    public void setUp() throws Exception {
        soldProduct = new SoldProduct();

        soldProduct.setCustomer(Mockito.mock(Customer.class));
        soldProduct.setProduct(Mockito.mock(Product.class));
        soldProduct.setOrder(Mockito.mock(Order.class));
    }

    @After
    public void tearDown() throws Exception {
        soldProduct = null;
    }

    @Test
    public void getAll() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(soldProduct));

        assertEquals(1, soldProductService.getAll().size());
    }

    @Test
    public void getByProduct() throws Exception {
        when(repository.findAllByProduct(Mockito.mock(Product.class))).thenReturn(
                Collections.singletonList(soldProduct));

        List<SoldProduct> temp = soldProductService.getByProduct(Mockito.mock(Product.class));

        List<SoldProduct> sold = new ArrayList<>();
        sold.add(soldProduct);
        assertEquals(sold, temp);
    }

    @Test
    public void getByCustomerAndProduct() throws Exception {

    }

    @Test
    public void getByCustomer() throws Exception {

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