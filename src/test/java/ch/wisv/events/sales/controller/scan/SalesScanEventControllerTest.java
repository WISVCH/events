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
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * SalesScanEventControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesScanEventControllerTest extends ControllerTest {

    @Test
    public void testScanner() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(get("/sales/scan/event/" + event.getKey() + "/barcode"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/event/barcode"))
                .andExpect(model().attribute("event", event));
    }

    @Test
    public void testScannerEventNotExists() throws Exception {
        mockMvc.perform(get("/sales/scan/event/" + UUID.randomUUID().toString() + "/barcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/"));
    }

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
                post("/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", barcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/success"))
                .andExpect(flash().attribute("redirect", "/sales/scan/event/" + event.getKey() + "/barcode"))
                .andExpect(flash().attribute("ticket", ticket));
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
                post("/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", barcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/double"))
                .andExpect(flash().attribute("redirect", "/sales/scan/event/" + event.getKey() + "/barcode"))
                .andExpect(flash().attribute("ticket", ticket));
    }

    @Test
    public void testBarcodeScannerNotExists() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", "9780201379624"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/error"))
                .andExpect(flash().attribute("error", "Ticket 137962 does not exists"));
    }

    @Test
    public void testBarcodeScannerInvalidLength() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/sales/scan/event/" + event.getKey() + "/barcode")
                        .param("barcode", "978020137962"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/error"))
                .andExpect(flash().attribute("error", "Invalid EAN 13 barcode length!"));
    }

    @Test
    public void testCodeScanner() throws Exception {
        Event event = this.createEvent();
        Product product = this.createProduct();
        event.addProduct(product);
        eventRepository.saveAndFlush(event);

        Order order = this.createOrder(createCustomer(), ImmutableList.of(product), OrderStatus.PAID, "sales-scan-test");
        Ticket ticket = ticketService.createByOrder(order).get(0);

        mockMvc.perform(
                post("/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", ticket.getUniqueCode()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/success"))
                .andExpect(flash().attribute("redirect", "/sales/scan/event/" + event.getKey() + "/code"))
                .andExpect(flash().attribute("ticket", ticket));
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
                post("/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", ticket.getUniqueCode()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/double"))
                .andExpect(flash().attribute("redirect", "/sales/scan/event/" + event.getKey() + "/code"))
                .andExpect(flash().attribute("ticket", ticket));
    }

    @Test
    public void testCodeScannerNotExists() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/error"))
                .andExpect(flash().attribute("error", "Ticket 123456 does not exists"));
    }

    @Test
    public void testCodeScannerInvalidLength() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(
                post("/sales/scan/event/" + event.getKey() + "/code")
                        .param("code", "12456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/ticket/error"))
                .andExpect(flash().attribute("error", "Invalid unique code length!"));
    }
}