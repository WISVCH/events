package ch.wisv.events.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.Optional;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopPaymentControllerTest extends ControllerTest {

    @Test
    public void testPaymentOverview() throws Exception {
        Order order = new Order();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/payment/index"))
                .andExpect(model().attribute("order", order))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/payment/ideal\"")))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/payment/reservation\"")));
    }

    @Test
    public void testPaymentOverviewNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/payment/index"))
                .andExpect(model().attribute("order", order))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/payment/ideal\"")))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/payment/reservation\"")));
    }

    @Test
    public void testPaymentOverviewNotSuitableForCheckout() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/payment/index"))
                .andExpect(model().attribute("order", order))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/payment/ideal\"")))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/payment/reservation\"")));
    }

    @Test
    public void testPaymentReservation() throws Exception {
        Order order = new Order();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/reservation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/return/*/reservation"));

        Order optional = orderService.getByReference(order.getPublicReference());
        assertEquals(OrderStatus.RESERVATION, optional.getStatus());
    }

    @Test
    public void testCheckoutIdeal() {
    }

    @Test
    public void testReturnMolliePayment() {
    }
}