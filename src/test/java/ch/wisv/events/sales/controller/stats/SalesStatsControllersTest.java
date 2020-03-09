package ch.wisv.events.sales.controller.stats;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;


/**
 * SalesScanMainControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesStatsControllersTest extends ControllerTest {

    /**
     * Event used during testing
     */
    private Event event;

    /**
     * Order used during testing
     */
    private Order order;

    /**
     * Sets up the items
     */
    @Before
    public void localSetUp() {
        Customer customer = createCustomer();
        event = createEvent();
        Product product = createProduct();
        event.addProduct(product);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        order = createOrder(customer, productList, OrderStatus.PAID, "");
        OrderProduct orderProduct = createOrderProduct(product);
        order.addOrderProduct(orderProduct);
    }

    /**
     * teardown of testing setup
     */
    @After
    public void localTearDown() {
        event = null;
        order = null;
    }

    /**
     * Test stats event controller
     *
     * @throws Exception when exception
     */
    @Test
    public void eventsControllerTest() throws Exception {
        Event event = createEvent();
        mockMvc.perform(get("/sales/stats/event/" + event.getKey()))
            .andExpect(status().isOk())
            .andExpect(view().name("sales/stats/event/index"))
            .andExpect(model().attributeExists("event", "moneyEarned", "ticketsSold", "orders"));
    }

    /**
     * Test stats event listings page
     *
     * @throws Exception when exception
     */
    @Test
    public void mainControllerTest() throws Exception {
        mockMvc.perform(get("/sales/stats/"))
            .andExpect(status().isOk())
            .andExpect(view().name("sales/stats/index"))
            .andExpect(model().attributeExists("events"));
    }

    /**
     * Test stats product controller
     *
     * @throws Exception when exception
     */
    @Test
    public void productControllerTest() throws Exception {
        Event event = createEvent();

        mockMvc.perform(get("/sales/stats/products/" + event.getKey()))
            .andExpect(status().isOk())
            .andExpect(view().name("sales/stats/products/index"))
            .andExpect(model().attributeExists("products"))
            .andExpect(model().attribute("products", is(event.getProducts())));
    }

    /**
     * Tests the event not found redirect
     *
     * @throws Exception when exception
     */
    @Test
    public void eventNotFoundRedirectTest() throws Exception {
        mockMvc.perform(get("/sales/stats/event/____"))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/sales/stats/"));
    }
}
