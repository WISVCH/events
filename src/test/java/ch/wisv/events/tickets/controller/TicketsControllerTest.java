package ch.wisv.events.tickets.controller;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.tickets.service.TicketsService;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
@RunWith(SpringRunner.class)
@WebMvcTest(TicketsController.class)
public class TicketsControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected ProductService productService;

    @MockBean
    protected TicketsService ticketsService;

    @MockBean
    protected OrderService orderService;

    @Test
    public void testGetIndex() throws Exception {
        Customer customer = new Customer("Sven Popping", "test@ch.tudelft.nl", "test", "123");
        Product product = new Product();

        when(ticketsService.getCurrentCustomer()).thenReturn(customer);
        when(productService.getAvailableProducts()).thenReturn(ImmutableList.of(product));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/index"))
                .andExpect(model().attribute("orderProduct", hasProperty("products", is(new HashMap<String, Long>()))))
                .andExpect(model().attribute("customer", is(customer)))
                .andExpect(model().attribute("products", is(ImmutableList.of(product))));
    }

    @Test
    public void testPostCheckout() throws Exception {
        // Create needed objects
        Customer customer = new Customer("Sven Popping", "test@ch.tudelft.nl", "test", "123");
        Product product = new Product("test", "test ticket", 1.33d, 100, LocalDateTime.now(), LocalDateTime.now());
        HashMap<String, Long> products = new HashMap<>();
        products.put(product.getKey(), 2L);

        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProducts(products);

        Order order = new Order();
        order.addOrderProduct(new OrderProduct(product, product.getCost(), 2L));

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(customer);
        when(orderService.createOrderByOrderProductDTO(orderProductDTO)).thenReturn(order);
        doNothing().when(orderService).assertIsValidForCustomer(order);
        doNothing().when(orderService).create(order);

        // Perform call
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("products['" + product.getKey() + "']", "2")
                .sessionAttr("orderProduct", new OrderProductDTO())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/"));

        // Verify service calls
        verify(orderService, times(1)).createOrderByOrderProductDTO(orderProductDTO);
        verify(orderService, times(1)).assertIsValidForCustomer(order);
        verify(orderService, times(1)).create(order);
        verify(ticketsService, times(1)).getCurrentCustomer();
    }

    @Test
    public void testPostCheckoutProductNotFound() throws Exception {
        // Create needed objects
        HashMap<String, Long> products = new HashMap<>();
        products.put("key", 2L);

        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProducts(products);

        // Set up mock calls
        when(orderService.createOrderByOrderProductDTO(orderProductDTO)).thenThrow(new ProductNotFoundException("key key"));

        // Perform call
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("products['key']", "2")
                .sessionAttr("orderProduct", new OrderProductDTO())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Product with key key not found!")));
    }

    @Test
    public void testPostCheckoutInvalid() throws Exception {
        // Create needed objects
        Customer customer = new Customer("Sven Popping", "test@ch.tudelft.nl", "test", "123");
        Product product = new Product("test", "test ticket", 1.33d, 100, LocalDateTime.now(), LocalDateTime.now());
        HashMap<String, Long> products = new HashMap<>();
        products.put(product.getKey(), 2L);

        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProducts(products);

        Order order = new Order();
        order.addOrderProduct(new OrderProduct(product, product.getCost(), 2L));

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(customer);
        when(orderService.createOrderByOrderProductDTO(orderProductDTO)).thenReturn(order);
        doThrow(new OrderInvalidException("Invalid order")).when(orderService).assertIsValidForCustomer(order);
        doNothing().when(orderService).create(order);

        // Perform call
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("products['" + product.getKey() + "']", "2")
                .sessionAttr("orderProduct", new OrderProductDTO())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Invalid order")));


        // Verify Service calls
        verify(orderService, times(0)).create(order);
    }

    @Test
    public void testPostCheckoutEmptyBasket() throws Exception {
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("orderProduct", new OrderProductDTO())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Shopping basket can not be empty!")));
    }
}