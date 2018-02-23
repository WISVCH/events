package ch.wisv.events.tickets.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.*;
import ch.wisv.events.core.model.product.Product;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

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
@WebMvcTest(TicketsController.class)
public class TicketsControllerTest extends ControllerTest {

    private Customer customer;

    private Event event;

    private Product product;

    private Order order;

    private OrderProductDTO orderProductDTO;

    @Before
    public void setUp() throws Exception {
        this.customer = new Customer("", "Sven Popping", "test@ch.tudelft.nl", "test", "123");
        this.event = new Event("title event", "description", "location", 10, 10, "", LocalDateTime.now(), LocalDateTime.now(), "short description");
        this.product = new Product("test", "test ticket", 1.33d, 100, LocalDateTime.now(), LocalDateTime.now());
        this.event.addProduct(this.product);

        HashMap<String, Long> products = new HashMap<>();
        products.put(this.product.getKey(), 2L);

        this.orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProducts(products);

        this.order = new Order();
        order.addOrderProduct(new OrderProduct(product, product.getCost(), 2L));
    }

    @Test
    public void testGetIndex() throws Exception {
        when(ticketsService.getCurrentCustomer()).thenReturn(customer);
        when(eventService.getUpcomingEvents()).thenReturn(ImmutableList.of(event));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/index"))
                .andExpect(model().attribute("orderProduct", hasProperty("products", is(new HashMap<String, Long>()))))
                .andExpect(model().attribute("customer", is(customer)))
                .andExpect(model().attribute("events", is(ImmutableList.of(event))));
    }

    @Test
    public void testPostCheckout() throws Exception {
        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.createOrderByOrderProductDTO(any(OrderProductDTO.class))).thenReturn(this.order);
        doNothing().when(orderService).assertIsValidForCustomer(this.order);
        doNothing().when(orderService).create(this.order);

        // Perform call
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("products['" + this.product.getKey() + "']", "2")
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
        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.createOrderByOrderProductDTO(any(OrderProductDTO.class))).thenThrow(new ProductNotFoundException("key key"));

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
        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.createOrderByOrderProductDTO(any(OrderProductDTO.class))).thenReturn(this.order);
        doThrow(new OrderInvalidException("Invalid order")).when(orderService).assertIsValidForCustomer(this.order);

        // Perform call
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("products['" + this.product.getKey() + "']", "2")
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
        // Perform call
        mockMvc.perform(post("/checkout/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("orderProduct", new OrderProductDTO())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Shopping basket can not be empty!")));
    }

    @Test
    public void testGetCheckout() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/checkout/" + this.order.getPublicReference() + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/checkout"))
                .andExpect(model().attribute("order", is(this.order)));
    }

    @Test
    public void testGetCheckoutOrderNotFound() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenThrow(new OrderNotFoundException("key " + this.order.getPublicReference()));

        // Perform call
        mockMvc.perform(get("/checkout/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Order with key " + this.order.getPublicReference() + " not found!")));
    }

    @Test
    public void testGetCheckoutAccessDenied() throws Exception {
        thrown.expect(NestedServletException.class);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/checkout/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetCancel() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        doNothing().when(orderService).updateOrderStatus(this.order, OrderStatus.CANCELLED);

        // Perform call
        mockMvc.perform(get("/cancel/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(orderService, times(1)).updateOrderStatus(this.order, OrderStatus.CANCELLED);
    }

    @Test
    public void testGetCancelOrderNotFound() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenThrow(new OrderNotFoundException("key " + this.order.getPublicReference()));

        // Perform call
        mockMvc.perform(get("/cancel/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Order with key " + this.order.getPublicReference() + " not found!")));
    }

    @Test
    public void testGetCancelAccessDenied() throws Exception {
        thrown.expect(NestedServletException.class);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/cancel/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is4xxClientError());

        verify(orderService, times(0)).updateOrderStatus(this.order, OrderStatus.CANCELLED);
    }

    @Test
    public void testGetPayment() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        this.order.setAmount(1.0d);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        when(ticketsService.getPaymentsMollieUrl(this.order)).thenReturn("url.to.payments");

        // Perform call
        mockMvc.perform(get("/payment/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("url.to.payments"));

        verify(ticketsService, times(1)).getPaymentsMollieUrl(this.order);
    }

    @Test
    public void testGetPaymentFreeOrder() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        this.order.setAmount(0.0d);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        when(ticketsService.getPaymentsMollieUrl(this.order)).thenReturn("url.to.payments");
        doNothing().when(orderService).updateOrderStatus(this.order, OrderStatus.PAID_IDEAL);

        // Perform call
        mockMvc.perform(get("/payment/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/complete/" + this.order.getPublicReference() + "/"));

        verify(orderService, times(1)).updateOrderStatus(this.order, OrderStatus.PAID_IDEAL);
    }

    @Test
    public void testGetPaymentOrderNotFound() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenThrow(new OrderNotFoundException("key " + this.order.getPublicReference()));

        // Perform call
        mockMvc.perform(get("/payment/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Order with key " + this.order.getPublicReference() + " not found!")));
    }

    @Test
    public void testGetPaymentAccessDenied() throws Exception {
        thrown.expect(NestedServletException.class);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/payment/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is4xxClientError());

        verify(ticketsService, times(0)).getPaymentsMollieUrl(this.order);
    }

    @Test
    public void testGetPaymentPaymentsFails() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        this.order.setAmount(1.0d);

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        when(ticketsService.getPaymentsMollieUrl(this.order)).thenThrow(new PaymentsConnectionException());

        // Perform call
        mockMvc.perform(get("/payment/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + this.order.getPublicReference() + "/"))
                .andExpect(flash().attribute("error", is("Something went wrong trying to fetch the payment status.")));

        verify(ticketsService, times(1)).getPaymentsMollieUrl(this.order);
    }

    @Test
    public void testGetStatus() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        this.order.setStatus(OrderStatus.PAID_IDEAL);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        doNothing().when(ticketsService).updateOrderStatus(this.order, key);

        // Perform call
        mockMvc.perform(get("/status/" + this.order.getPublicReference() + "/").param("reference", key))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/complete/" + this.order.getPublicReference() + "/"));

        verify(ticketsService, times(1)).updateOrderStatus(this.order, key);
    }

    @Test
    public void testGetStatusWaiting() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        this.order.setStatus(OrderStatus.WAITING);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        doNothing().when(ticketsService).updateOrderStatus(this.order, key);

        // Perform call
        mockMvc.perform(get("/status/" + this.order.getPublicReference() + "/").param("reference", key))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/complete/" + this.order.getPublicReference() + "/"));

        verify(ticketsService, times(6)).updateOrderStatus(this.order, key);
    }

    @Test
    public void testGetStatusOrderNotFound() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenThrow(new OrderNotFoundException("key " + this.order.getPublicReference()));

        // Perform call
        mockMvc.perform(get("/status/" + this.order.getPublicReference() + "/").param("reference", key))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Order with key " + this.order.getPublicReference() + " not found!")));
    }

    @Test
    public void testGetStatusPaymentsFails() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);
        doThrow(new PaymentsConnectionException()).when(ticketsService).updateOrderStatus(this.order, key);

        // Perform call
        mockMvc.perform(get("/status/" + this.order.getPublicReference() + "/").param("reference", key))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + this.order.getPublicReference() + "/"))
                .andExpect(flash().attribute("error", is("Something went wrong trying to fetch the payment status.")));
    }

    @Test
    public void testGetStatusAccessDenied() throws Exception {
        thrown.expect(NestedServletException.class);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/status/" + this.order.getPublicReference() + "/").param("reference", key))
                .andExpect(status().is4xxClientError());

        verify(ticketsService, times(0)).updateOrderStatus(this.order, key);
    }

    @Test
    public void testGetComplete() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/complete/" + this.order.getPublicReference() + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/complete"));
    }

    @Test
    public void testGetCompleteOrderNotFound() throws Exception {
        // Set customer
        this.order.setCustomer(this.customer);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenThrow(new OrderNotFoundException("key " + this.order.getPublicReference()));

        // Perform call
        mockMvc.perform(get("/complete/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", is("Order with key " + this.order.getPublicReference() + " not found!")));
    }

    @Test
    public void testGetCompleteAccessDenied() throws Exception {
        thrown.expect(NestedServletException.class);
        String key = UUID.randomUUID().toString();

        // Set up mock calls
        when(ticketsService.getCurrentCustomer()).thenReturn(this.customer);
        when(orderService.getByReference(this.order.getPublicReference())).thenReturn(this.order);

        // Perform call
        mockMvc.perform(get("/complete/" + this.order.getPublicReference() + "/"))
                .andExpect(status().is4xxClientError());

        verify(ticketsService, times(0)).updateOrderStatus(this.order, key);
    }
}