package ch.wisv.events.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        order.getOrderProducts().get(0).getProduct().setReservable(true);


        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/payment/index"))
                .andExpect(model().attribute("order", order))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/mollie\"")))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/reservation\"")));
    }

    @Test
    public void testNonReservableOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.getOrderProducts().get(0).getProduct().setReservable(false);

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment/mollie"));
    }

    @Test
    public void testOrderWithRedirectLink() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PAID, "events-webshop");
        order.getOrderProducts().get(0).getProduct().setRedirectUrl("https://test.nl");

        mockMvc.perform(get("/return/" + order.getPublicReference()))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/return/success"))
                .andExpect(content().string(containsString("href=\"https://test.nl\"")));
    }

    @Test
    public void testOrderWithoutRedirectLink() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PAID, "events-webshop");
        order.getOrderProducts().get(0).getProduct().setRedirectUrl(null);

        mockMvc.perform(get("/return/" + order.getPublicReference()))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/return/success"))
                .andExpect(content().string(not(containsString("href=\"https://test.nl\""))));
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

        this.requestPaymentCheckoutException(order, "/payment", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentOverviewWrongCreatedBy() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");

        this.requestPaymentCheckoutException(order, "/payment", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentOverviewWrongMissingOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");
        order.setOwner(null);

        this.requestPaymentCheckoutException(order, "/payment", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentOverviewMissingProducts() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setOrderProducts(new ArrayList<>());

        this.requestPaymentCheckoutException(order, "/payment", "Order is not suitable for checkout!");
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
                .andExpect(redirectedUrlPattern("/return/*"));

        Order optional = orderService.getByReference(order.getPublicReference());
        assertEquals(OrderStatus.RESERVATION, optional.getStatus());
    }

    @Test
    public void testPaymentReservationWrongStatus() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ANONYMOUS, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentReservationWrongCreatedBy() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentReservationWrongMissingOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");
        order.setOwner(null);

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentReservationMissingProducts() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setOrderProducts(new ArrayList<>());

        this.requestPaymentCheckoutException(order, "/payment/reservation", "Order is not suitable for checkout!");
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

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/mollie"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://paymentURL.com"));

        Order optional = orderService.getByReference(order.getPublicReference());

        assertEquals(OrderStatus.PENDING, optional.getStatus());
        assertEquals(PaymentMethod.MOLLIE, optional.getPaymentMethod());
    }

    @Test
    public void testPaymentMollieWrongStatus() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ANONYMOUS, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/mollie", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentMollieWrongCreatedBy() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");

        this.requestPaymentCheckoutException(order, "/payment/mollie", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentMollieWrongMissingOrder() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "somebody");
        order.setOwner(null);

        this.requestPaymentCheckoutException(order, "/payment/mollie", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentMollieMissingProducts() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.ASSIGNED, "events-webshop");
        order.setOrderProducts(new ArrayList<>());

        this.requestPaymentCheckoutException(order, "/payment/mollie", "Order is not suitable for checkout!");
    }

    @Test
    public void testPaymentMollieNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/payment/mollie"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference " + order.getPublicReference() + " not found!"));
    }

    @Test
    public void testPaymentMollieNotSuitableForCheckout() throws Exception {
        Order order = this.createPaymentOrder(OrderStatus.PAID, "events-webshop");

        this.requestPaymentCheckoutException(order, "/payment/mollie", "Order with status PAID is not suitable for checkout");
    }

    private void requestPaymentCheckoutException(Order order, String path, String error) throws Exception {
        mockMvc.perform(get("/checkout/" + order.getPublicReference() + path))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", error));
    }
}