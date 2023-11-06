package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import org.json.simple.JSONObject;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardSalesexportControllerTest extends ControllerTest {

    @Test
    public void testCsvExport() throws Exception {
        Product product = this.createProduct();
        Event event = this.createEvent();
        event.addProduct(product);
        Customer customer = this.createCustomer();
        Order order = this.createOrder(customer, ImmutableList.of(product), OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order, OrderStatus.PAID);

        Order order1 = this.createOrder(customer, ImmutableList.of(product), OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order1, OrderStatus.PAID);

        mockMvc.perform(get("/administrator/salesexport/csv"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("text/csv"));
    }
}