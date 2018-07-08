package ch.wisv.events.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import java.util.ArrayList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopPaymentControllerTest extends ControllerTest {

    /**
     * Overview tests
     */
    @Test
    public void testPaymentOverview() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/payment/index"))
                .andExpect(model().attribute("order", order))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/ideal\"")))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/reservation\"")));
    }

    @Test
    public void testPaymentOverviewOrderAmountZero() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setAmount(0.d);
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/return/" + order.getPublicReference()));
    }

    @Test
    public void testPaymentOverviewWrongStatus() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ANONYMOUS, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment", "Order status must be ASSIGNED before payment");
    }

    @Test
    public void testPaymentOverviewWrongCreatedBy() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");

        this.requestPaymentCheckoutException(order, "/payment", "Order created by must be set before payment");
    }

    @Test
    public void testPaymentOverviewWrongMissingOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");
        order.setOwner(null);

        this.requestPaymentCheckoutException(order, "/payment", "Order owner must be set before payment");
    }

    @Test
    public void testPaymentOverviewMissingProducts() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setOrderProducts(new ArrayList<>());

        this.requestPaymentCheckoutException(order, "/payment", "Order must contain products before payment");
    }

    @Test
    public void testPaymentOverviewNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference " + order.getPublicReference() + " not found!"));
    }

    @Test
    public void testPaymentOverviewNotSuitableForCheckout() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PAID, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment", "Order with status PAID is not suitable for checkout");
    }

    /**
     * Reservation tests
     */
    @Test
    public void testPaymentReservation() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/reservation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/return/*/reservation"));

        Order optional = orderService.getByReference(order.getPublicReference());
        assertEquals(OrderStatus.RESERVATION, optional.getStatus());
    }

    @Test
    public void testPaymentReservationWrongStatus() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ANONYMOUS, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order status must be ASSIGNED before payment");
    }

    @Test
    public void testPaymentReservationWrongCreatedBy() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order created by must be set before payment");
    }

    @Test
    public void testPaymentReservationWrongMissingOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");
        order.setOwner(null);

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order owner must be set before payment");
    }

    @Test
    public void testPaymentReservationMissingProducts() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setOrderProducts(new ArrayList<>());

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order must contain products before payment");
    }

    @Test
    public void testPaymentReservationNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/reservation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference " + order.getPublicReference() + " not found!"));
    }

    @Test
    public void testPaymentReservationNotSuitableForCheckout() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PAID, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order with status PAID is not suitable for checkout");
    }

    /**
     * Ideal tests
     */
    @Test
    public void testPaymentIdeal() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/ideal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://paymentURL.com"));

        Order optional = orderService.getByReference(order.getPublicReference());

        assertEquals(OrderStatus.PENDING, optional.getStatus());
        assertEquals(PaymentMethod.IDEAL, optional.getPaymentMethod());
    }

    @Test
    public void testPaymentIdealWrongStatus() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ANONYMOUS, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/ideal", "Order status must be ASSIGNED before payment");
    }

    @Test
    public void testPaymentIdealWrongCreatedBy() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");

        this.requestPaymentCheckoutException(order, "/payment/ideal", "Order created by must be set before payment");
    }

    @Test
    public void testPaymentIdealWrongMissingOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");
        order.setOwner(null);

        this.requestPaymentCheckoutException(order, "/payment/ideal", "Order owner must be set before payment");
    }

    @Test
    public void testPaymentIdealMissingProducts() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setOrderProducts(new ArrayList<>());

        this.requestPaymentCheckoutException(order, "/payment/ideal", "Order must contain products before payment");
    }

    @Test
    public void testPaymentIdealNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/ideal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference " + order.getPublicReference() + " not found!"));
    }

    @Test
    public void testPaymentIdealNotSuitableForCheckout() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PAID, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/ideal", "Order with status PAID is not suitable for checkout");
    }

    /**
     * Return after Mollie payment tests
     */
    @Test
    public void testReturnAfterMolliePayment() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PENDING, "events-webshop");
        order.setPaymentMethod(PaymentMethod.IDEAL);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/return")
                                .param("reference", "123-345-562"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/return/" + order.getPublicReference()));

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(PaymentMethod.IDEAL, order.getPaymentMethod());
    }

    @Test
    public void testReturnAfterMolliePaymentNotFound() throws Exception {
        mockMvc.perform(get("/checkout/1345-4567-5678/payment/return")
                                .param("reference", "123-345-562"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testReturnAfterMolliePaymentNotPending() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setPaymentMethod(PaymentMethod.IDEAL);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/return")
                                .param("reference", "123-345-562"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(flash().attribute("error", "Order is in an invalid state."));
    }

    @Test
    public void testReturnAfterMolliePaymentError() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PENDING, "events-webshop");
        order.setPaymentMethod(PaymentMethod.IDEAL);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/return")
                                .param("reference", "123-345-561"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/return/" + order.getPublicReference()));

        assertEquals(OrderStatus.ERROR, order.getStatus());
    }

    @Test
    public void testReturnAfterMolliePaymentFailure() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PENDING, "events-webshop");
        order.setPaymentMethod(PaymentMethod.IDEAL);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/return")
                                .param("reference", "123-345-564"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(flash().attribute("error", "Something went wrong trying to fetch the payment status."));
    }

    private void requestPaymentCheckoutException(Order order, String path, String error) throws Exception {
        mockMvc.perform(get("/checkout/" + order.getPublicReference() + path))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", error));
    }
}