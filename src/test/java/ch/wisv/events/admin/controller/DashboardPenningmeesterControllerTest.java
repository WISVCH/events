package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardPenningmeesterControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Product product = this.createProduct();
        Customer customer = this.createCustomer();
        Order order = this.createOrder(customer, ImmutableList.of(product), OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order, OrderStatus.PAID);

        Order order1 = this.createOrder(customer, ImmutableList.of(product), OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order1, OrderStatus.PAID);

        Map<LocalDate, Map<Product, Integer>> map = new HashMap<>();
        LocalDate date = order.getPaidAt().toLocalDate();
        map.put(LocalDate.of(date.getYear(), date.getMonthValue(), 1), ImmutableMap.of(product, 2));

        mockMvc.perform(get("/administrator/penningmeester"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/penningmeester/index"))
                .andExpect(model().attribute("productMap", map));
    }
}