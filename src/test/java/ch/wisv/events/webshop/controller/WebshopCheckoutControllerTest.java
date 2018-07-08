package ch.wisv.events.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopCheckoutControllerTest extends ControllerTest {

    @Test
    public void testCheckoutShoppingBasket() throws Exception {
        Product product = new Product("test", "test ticket", 1.33d, 100, LocalDateTime.now(), LocalDateTime.now());
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post("/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products['" + product.getKey() + "']", "2")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/checkout/*"));

        Order order = orderRepository.findAll().get(0);

        assertEquals("events-webshop", order.getCreatedBy());
        assertEquals(OrderStatus.ANONYMOUS, order.getStatus());
    }

    @Test
    public void testCheckoutShoppingBasketEmptyBasket() throws Exception {
        mockMvc.perform(
                post("/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Shopping basket can not be empty!"));
    }

    @Test
    public void testCheckoutShoppingBasketExceedProductLimit() throws Exception {
        Product product = new Product("test", "test ticket", 1.33d, 2, LocalDateTime.now(), LocalDateTime.now());
        productRepository.saveAndFlush(product);

        Product product2 = new Product("test", "test ticket", 1.33d, null, LocalDateTime.now(), LocalDateTime.now());
        productRepository.saveAndFlush(product2);

        mockMvc.perform(
                post("/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products['" + product2.getKey() + "']", "2")
                        .param("products['" + product.getKey() + "']", "3")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Product limit exceeded (max 2 tickets allowed)."));
    }

    @Test
    public void testCheckoutShoppingBasketExceedProductLimitEnforcingRightLimit() throws Exception {
        Product product = new Product("test", "test ticket", 1.33d, 2, LocalDateTime.now(), LocalDateTime.now());
        productRepository.saveAndFlush(product);

        Product product2 = new Product("test", "test ticket", 1.33d, 10, LocalDateTime.now(), LocalDateTime.now());
        productRepository.saveAndFlush(product2);

        mockMvc.perform(
                post("/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products['" + product.getKey() + "']", "1")
                        .param("products['" + product2.getKey() + "']", "3")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/checkout/*"));
    }

    @Test
    public void testCheckoutShoppingBasketExceedEventLimit() throws Exception {
        Product product = new Product("test", "test ticket", 1.33d, 20, LocalDateTime.now(), LocalDateTime.now());
        productRepository.saveAndFlush(product);

        Event event = new Event(
                "title event",
                "description",
                "location",
                10,
                10,
                "",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "short description"
        );
        event.addProduct(product);
        eventService.create(event);

        mockMvc.perform(
                post("/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products['" + product.getKey() + "']", "11")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Event limit exceeded (max 10 tickets allowed)."));
    }

    @Test
    public void testCheckoutShoppingBasketProductNotExists() throws Exception {
        mockMvc.perform(
                post("/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products['123-345-567']", "2")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Product with key 123-345-567 not found!"));
    }

    @Test
    public void testCheckoutOverview() throws Exception {
        Order order = new Order();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference()))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/checkout/index"))
                .andExpect(model().attribute("order", order));
    }

    @Test
    public void testCheckoutOverviewOrderNotSuitableForCheckout() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with status PAID is not suitable for checkout"));
    }

    @Test
    public void testCheckoutOverviewOrderNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference " + order.getPublicReference() + " not found!"));
    }

    @Test
    public void testCheckoutCancel() throws Exception {
        Order order = new Order();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("success", "Order has successfully been cancelled."));

        Order order1 = orderService.getByReference(order.getPublicReference());
        assertEquals(OrderStatus.CANCELLED, order1.getStatus());
    }

    @Test
    public void testCheckoutCancelOrderNotSuitableForCheckout() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with status PAID is not suitable for checkout"));
    }

    @Test
    public void testCheckoutCancelOrderNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference " + order.getPublicReference() + " not found!"));
    }
}