package ch.wisv.events.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.ArrayList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopCustomerControllerTest extends ControllerTest {

    /**
     * Customer options tests
     */
    @Test
    public void testCustomerOptions() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ANONYMOUS, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/checkout/customer"))
                .andExpect(model().attribute("order", order))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/customer/chconnect\"")))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/customer/guest\"")));
    }

    /**
     * Customer chconnect tests
     */
    @Test
    public void testCustomerChconnectOptions() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ANONYMOUS, "events-webshop");

        // Expect redirect to chconnect
        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/customer/chconnect"));
    }

    @Test
    public void testCustomerOptionsNotAnonymous() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ASSIGNED, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment"));
    }

    @Test
    public void testCustomerOptionsNotSuitable() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.PAID, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with status PAID is not suitable for checkout"));
    }

    @Test
    public void testCustomerGuestOptionsAssigned() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ASSIGNED, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment"));
    }

    /**
     * GET Customer guest tests
     */
    @Test
    public void testCustomerGuest() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ANONYMOUS, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer/guest"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/checkout/create"))
                .andExpect(model().attribute("order", order))
                .andExpect(model().attribute("customer", instanceOf(Customer.class)))
                .andExpect(content().string(containsString("action=\"/checkout/" + order.getPublicReference() + "/customer/guest\"")));
    }

    @Test
    public void testCustomerGuestOrderAssigned() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ASSIGNED, "events-webshop");

        mockMvc.perform(get("/checkout/" + order.getPublicReference() + "/customer/guest"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment"));
    }

    @Test
    public void testCustomerGuestOrderNotFound() throws Exception {
        mockMvc.perform(get("/checkout/12345-3456-5678/customer/guest"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Order with reference 12345-3456-5678 not found!"));
    }

    /**
     * POST Customer guest tests
     */
    @Test
    public void testPostCustomerGuest() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ANONYMOUS, "events-webshop");

        mockMvc.perform(post("/checkout/" + order.getPublicReference() + "/customer/guest")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", "Test Username")
                                .param("email", "email@ch.tudelft.nl")
                                .sessionAttr("customer", new Customer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/payment"));
    }

    @Test
    public void testPostCustomerGuestMissingName() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.ANONYMOUS, "events-webshop");

        mockMvc.perform(post("/checkout/" + order.getPublicReference() + "/customer/guest")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("email", "email@ch.tudelft.nl")
                                .sessionAttr("customer", new Customer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/" + order.getPublicReference() + "/customer/guest"));
    }

    @Test
    public void testPostCustomerGuestMissingEmail() throws Exception {
        Order order = this.createOrder(null, new ArrayList<>(), OrderStatus.PAID, "events-webshop");

        mockMvc.perform(post("/checkout/" + order.getPublicReference() + "/customer/guest")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", "Test Username")
                                .param("email", "email@ch.tudelft.nl")
                                .sessionAttr("customer", new Customer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}