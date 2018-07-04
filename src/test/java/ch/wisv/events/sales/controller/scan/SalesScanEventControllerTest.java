package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import java.util.UUID;
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
 * SalesScanEventControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesScanEventControllerTest extends ControllerTest {

    @Test
    public void scanner() throws Exception {
        Event event = createEvent();

        mockMvc.perform(get("/sales/scan/event/" + event.getKey() + "/barcode"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/event/barcode"))
                .andExpect(model().attribute("event", event));
    }

    @Test
    public void scannerEventNotExists() throws Exception {
        mockMvc.perform(get("/sales/scan/event/" + UUID.randomUUID().toString()  + "/barcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/"));
    }

    @Test
    public void barcodeScanner() {
    }

    @Test
    public void codeScanner() {
    }
}