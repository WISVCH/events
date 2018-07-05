package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.ticket.Ticket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * SalesScanTicketControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesScanTicketControllerTest extends ControllerTest {

    @Test
    public void testError() throws Exception {
        mockMvc.perform(get("/sales/scan/ticket/error")
                                .flashAttr("error", "This is an error")
                                .flashAttr("redirect", "/sales/scan/test/"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/ticket/error"))
                .andExpect(model().attribute("error", "This is an error"))
                .andExpect(model().attribute("redirect", "/sales/scan/test/"));
    }

    @Test
    public void testErrorMissingRedirect() throws Exception {
        mockMvc.perform(get("/sales/scan/ticket/error")
                                .flashAttr("error", "This is an error"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/ticket/error"))
                .andExpect(model().attribute("error", "This is an error"))
                .andExpect(model().attribute("redirect", "/sales/scan/"));
    }

    @Test
    public void testErrorMissingError() throws Exception {
        mockMvc.perform(get("/sales/scan/ticket/error"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/"));
    }

    @Test
    public void testIndex() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setProduct(createProduct());
        ticket.setOwner(createCustomer());

        mockMvc.perform(get("/sales/scan/ticket/success")
                                .flashAttr("ticket", ticket)
                                .flashAttr("redirect", "/sales/scan/test/"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/ticket/success"))
                .andExpect(model().attribute("ticket", ticket))
                .andExpect(model().attribute("redirect", "/sales/scan/test/"));
    }

    @Test
    public void testIndexMissingRedirect() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setProduct(createProduct());
        ticket.setOwner(createCustomer());

        mockMvc.perform(get("/sales/scan/ticket/success")
                                .flashAttr("ticket", ticket))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/ticket/success"))
                .andExpect(model().attribute("ticket", ticket))
                .andExpect(model().attribute("redirect", "/sales/scan/"));
    }

    @Test
    public void testIndexMissingTicket() throws Exception {
        mockMvc.perform(get("/sales/scan/ticket/success"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/"));
    }

    @Test
    public void testIndexRandom() throws Exception {
        mockMvc.perform(get("/sales/scan/ticket/random"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/"));
    }
}