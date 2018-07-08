package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SalesScanEventControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesScanEventControllerTest extends ControllerTest {

    @Test
    public void testScanner() throws Exception {
        // TODO: fix this test with csrf enabled.

        //        Event event = this.createEvent();
        //
        //        mockMvc.perform(get("/sales/scan/event/" + event.getKey() + "/barcode").with(csrf()))
        //                .andExpect(status().isOk())
        //                .andExpect(view().name("sales/scan/event/barcode"))
        //                .andExpect(model().attribute("event", event));
    }

    @Test
    public void testScannerEventNotExists() throws Exception {
        mockMvc.perform(get("/sales/scan/event/" + UUID.randomUUID().toString() + "/barcode"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/scan/"));
    }
}