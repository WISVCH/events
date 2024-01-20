package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.admin.TreasurerData;
import ch.wisv.events.admin.utils.AggregatedProduct;
import ch.wisv.events.utils.LdapGroup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import org.json.simple.JSONObject;



// import com.google.common.collect.Lists;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
public class DashboardSalesExportControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/administrator/salesexport"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/salesexport/index"));
    }

    @Test
    public void testAggregateOrders() throws Exception {
        Event event = this.createEvent();
        Customer customer = this.createCustomer();

        Product product1 = this.createProduct();
        product1.setCost(0.10d);
        event.addProduct(product1);
        
        Order order1 = this.createOrderSingleProduct(customer, product1, 2L, OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order1, OrderStatus.PAID);
        Order order2 = this.createOrderSingleProduct(customer, product1, 1L, OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order2, OrderStatus.PAID);

        Product product2 = this.createProduct();
        product2.setCost(13.13d);
        event.addProduct(product2);

        Order order3 = this.createOrderSingleProduct(customer, product2, 1L, OrderStatus.PENDING, "test");
        orderService.updateOrderStatus(order3, OrderStatus.PAID);
        // order4 should not be in the aggregated product, because OrderStatus is not set to PAID
        Order order4 = this.createOrderSingleProduct(customer, product2, 1L, OrderStatus.PENDING, "test");

        List<Integer> paymentMethods = new ArrayList<>();
        paymentMethods.add(PaymentMethod.IDEAL.toInt());
        paymentMethods.add(0);
        paymentMethods.add(1);
        paymentMethods.add(2);
        paymentMethods.add(3);
        paymentMethods.add(4);
        paymentMethods.add(5);
        List<TreasurerData> treasurerData = orderRepository.findallPaymentsByMonth(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), paymentMethods, false);
        List<TreasurerData> treasurerData2 = orderRepository.findallPayments();

        Map<Integer, AggregatedProduct> map = DashboardSalesExportController.aggregateOrders(treasurerData);

        
//        System.out.println("==================== print =====================");
//        System.out.println(treasurerData2.get(0).getPaidAt());
//        System.out.println(product1.getId());
//        System.out.println(map.keySet());
//        System.out.println(treasurerData);
        assertTrue(map.containsKey(product1.getId()));
        AggregatedProduct aggrProduct1 = map.get(product1.getId());
        assertEquals(event.toString(), aggrProduct1.eventTitle);
        assertEquals(event.getOrganizedBy().getName(), aggrProduct1.organizedBy);
        assertEquals(product1.getTitle(), aggrProduct1.productTitle);
        assertEquals((Double) (3*(product1.getCost())), aggrProduct1.totalIncome);
        assertEquals((Integer) 3, aggrProduct1.totalAmount);
        assertEquals(product1.getVatRate().name(), aggrProduct1.vatRate);
        assertEquals(product1.getCost(), aggrProduct1.price);

        AggregatedProduct aggrProduct2 = map.get(product2.getId());
        assertEquals(product2.getCost(), aggrProduct2.totalIncome);
        assertEquals((Integer) 1, aggrProduct2.totalAmount);


    }

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