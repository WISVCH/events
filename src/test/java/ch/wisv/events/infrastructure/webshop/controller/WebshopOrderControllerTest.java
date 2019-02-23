package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.domain.model.order.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * WebshopOrderControllerTest class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopOrderControllerTest extends ControllerTest {

    /**
     * Test order page when requested public reference does exists.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testShowOrderOverview() throws Exception {
        Order order = new Order();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/webshop/order/" + order.getPublicReference()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("webshop/webshop-checkout-order"))
                .andExpect(model().attribute("order", order));
    }

    /**
     * Test order page when requested public reference does not exists.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testOrderNotFound() throws Exception {
        Order order = new Order();

        mockMvc.perform(get("/webshop/order/" + order.getPublicReference()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("webshop/error/not-found"));
    }
}