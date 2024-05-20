package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardOrderControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.PAID, "tests");

        mockMvc.perform(get("/administrator/orders"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/orders/index"))
                .andExpect(model().attribute("orders", ImmutableList.of(order)));
    }


    @Test
    public void testView() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.PAID, "tests");

        mockMvc.perform(get("/administrator/orders/view/" + order.getPublicReference()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/orders/view"))
                .andExpect(model().attribute("order", order));
    }

    @Test
    public void testViewNotFound() throws Exception {
        mockMvc.perform(get("/administrator/orders/view/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/"))
                .andExpect(flash().attribute("error", "Order with reference not-found not found!"));
    }

    @Test
    public void testDelete() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.PAID, "tests");

        mockMvc.perform(get("/administrator/orders/delete/" + order.getPublicReference()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        mockMvc.perform(get("/administrator/orders/delete/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/"))
                .andExpect(flash().attribute("error", "Order with reference not-found not found!"));
    }

    @Test
    public void testDeleteAlreadyRejected() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.REJECTED, "tests");

        mockMvc.perform(get("/administrator/orders/delete/" + order.getPublicReference()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/"))
                .andExpect(flash().attribute("error", "Not allowed to update status from REJECTED to REJECTED"));
    }

    @Test
    public void testApprove() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.RESERVATION, "tests");

        mockMvc.perform(get("/administrator/orders/approve/" + order.getPublicReference() + "/IDEAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    public void testApproveNotFound() throws Exception {
        mockMvc.perform(get("/administrator/orders/approve/not-found/IDEAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/"))
                .andExpect(flash().attribute("error", "Order with reference not-found not found!"));
    }

    @Test
    public void testApproveInvalidPaymentMethod() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.RESERVATION, "tests");

        mockMvc.perform(get("/administrator/orders/approve/" + order.getPublicReference() + "/INVALID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testResendConfirmationMail() throws Exception {
        Order order = this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.RESERVATION, "tests");

        mockMvc.perform(get("/administrator/orders/resend-confirmation-mail/" + order.getPublicReference()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/view/" + order.getPublicReference()))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Order confirmation mail send!"));
    }

    @Test
    public void testResendConfirmationMailNotFound() throws Exception {
        this.createOrder(this.createCustomer(), ImmutableList.of(this.createProduct()), OrderStatus.RESERVATION, "tests");

        mockMvc.perform(get("/administrator/orders/resend-confirmation-mail/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/orders/view/not-found"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Order with reference not-found not found!"));
    }

}