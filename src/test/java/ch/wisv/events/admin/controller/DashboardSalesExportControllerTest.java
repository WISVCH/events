package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.admin.TreasurerData;
import ch.wisv.events.admin.utils.AggregatedProduct;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.Map;



import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        // this last order should not be in the aggregated product, because OrderStatus is not set to PAID
        this.createOrderSingleProduct(customer, product2, 1L, OrderStatus.PENDING, "test");

        List<Integer> paymentMethods = new ArrayList<>();
        paymentMethods.add(order1.getPaymentMethod().toInt());
        List<TreasurerData> treasurerData = orderRepository.findallPaymentsByMonth(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), paymentMethods, false);

        Map<Integer, AggregatedProduct> map = DashboardSalesExportController.aggregateOrders(treasurerData);

        
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
    public void testGenerateCsvContent() {
        // Mostly test if amounts are correctly rounded to 2 decimals

        Event event = this.createEvent();

        Product product1 = this.createProduct();
        product1.setCost(0.10d);
        event.addProduct(product1);

        AggregatedProduct aggregatedProduct1 = new AggregatedProduct();
        aggregatedProduct1.eventTitle = event.getTitle();
        aggregatedProduct1.organizedBy = event.getOrganizedBy().getName();
        aggregatedProduct1.productTitle = product1.getTitle();
        aggregatedProduct1.vatRate = product1.getVatRate().name();
        aggregatedProduct1.price = product1.getCost();
        aggregatedProduct1.totalIncome = product1.getCost();
        aggregatedProduct1.totalAmount = 1;

        aggregatedProduct1.totalAmount += 2;
        aggregatedProduct1.totalIncome += 2*product1.getCost();

        Product product2 = this.createProduct();
        product2.setCost(0.30d);
        event.addProduct(product1);

        AggregatedProduct aggregatedProduct2 = new AggregatedProduct();
        aggregatedProduct2.eventTitle = event.getTitle();
        aggregatedProduct2.organizedBy = event.getOrganizedBy().getName();
        aggregatedProduct2.productTitle = product2.getTitle();
        aggregatedProduct2.vatRate = product2.getVatRate().name();
        aggregatedProduct2.price = product2.getCost();
        aggregatedProduct2.totalIncome = 128.1;
        aggregatedProduct2.totalAmount = 123;

        aggregatedProduct2.totalAmount += 0;
        aggregatedProduct2.totalIncome += 0.2;

        Map<Integer, AggregatedProduct> map = new HashMap<>();

        map.put(product1.getId(), aggregatedProduct1);
        map.put(product2.getId(), aggregatedProduct2);

        String csvContent = DashboardSalesExportController.generateCsvContent(map);

        String expectedLine1 = event.getTitle()
                + ";" + event.getOrganizedBy().getName()
                + ";" + product1.getTitle()
                + ";" + "0.30"    // total income
                + ";" + "3"       // total amount
                + ";" + product1.getVatRate().name()
                + ";" + "0.10";

        String expectedLine2 = event.getTitle()
                + ";" + event.getOrganizedBy().getName()
                + ";" + product2.getTitle()
                + ";" + "128.30"    // total income
                + ";" + "123"       // total amount
                + ";" + product2.getVatRate().name()
                + ";" + "0.30";

        assertTrue(csvContent.contains(expectedLine1));
        assertTrue(csvContent.contains(expectedLine2));

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