package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.utils.Barcode;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesScanRestControllerTest extends ControllerTest {

    @Test
    public void testBarcodeScanner() throws Exception {
        Event event = this.createEvent();
        Product product = this.createProduct();
        event.addProduct(product);
        eventRepository.saveAndFlush(event);

        Order order = this.createOrder(createCustomer(), ImmutableList.of(product), OrderStatus.PAID, "sales-scan-test");
        Ticket ticket = ticketService.createByOrder(order).get(0);

        String barcode = RandomStringUtils.random(6, "0123456789") + ticket.getUniqueCode();
        barcode += Barcode.calculateChecksum(barcode.toCharArray());

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/barcode").with(csrf())
                        .param("barcode", barcode))
                .andExpect(status().is(200));
    }

    @Test
    public void testBarcodeScannerDouble() throws Exception {
        Event event = this.createEvent();
        Product product = this.createProduct();
        event.addProduct(product);
        eventRepository.saveAndFlush(event);

        Order order = this.createOrder(createCustomer(), ImmutableList.of(product), OrderStatus.PAID, "sales-scan-test");
        Ticket ticket = ticketService.createByOrder(order).get(0);
        ticketService.updateStatus(ticket, TicketStatus.SCANNED);

        String barcode = RandomStringUtils.random(6, "0123456789") + ticket.getUniqueCode();
        barcode += Barcode.calculateChecksum(barcode.toCharArray());

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", barcode))
                .andExpect(status().is(208));
    }

    @Test
    public void testBarcodeScannerNotExists() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", "9780201379624"))
                .andExpect(status().is(400));
    }

    @Test
    public void testBarcodeScannerInvalidLength() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", "978020137962"))
                .andExpect(status().is(400));
    }

    @Test
    public void testCodeScanner() throws Exception {
        Event event = this.createEvent();
        Product product = this.createProduct();
        event.addProduct(product);
        eventRepository.saveAndFlush(event);

        Order order = this.createOrder(createCustomer(), ImmutableList.of(product), OrderStatus.PAID, "sales-scan-test");
        Ticket ticket = ticketService.createByOrder(order).get(0);
        System.out.println("ticket = " + ticket.getStatus());

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", ticket.getUniqueCode()))
                .andExpect(status().is(200));
    }

    @Test
    public void testCodeScannerDouble() throws Exception {
        Event event = this.createEvent();
        Product product = this.createProduct();
        event.addProduct(product);
        eventRepository.saveAndFlush(event);

        Order order = this.createOrder(createCustomer(), ImmutableList.of(product), OrderStatus.PAID, "sales-scan-test");
        Ticket ticket = ticketService.createByOrder(order).get(0);
        ticketService.updateStatus(ticket, TicketStatus.SCANNED);

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", ticket.getUniqueCode()))
                .andExpect(status().is(208));
    }

    @Test
    public void testCodeScannerNotExists() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", "123456"))
                .andExpect(status().is(400));
    }

    @Test
    public void testCodeScannerInvalidLength() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/api/v1/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", "12456"))
                .andExpect(status().is(400));
    }
}