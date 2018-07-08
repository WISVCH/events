package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * SalesScanBarcodeControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SalesScanBarcodeControllerTest extends ControllerTest {

    /**
     * Test barcode scanner view
     *
     * @throws Exception when exception
     */
    @Test
    public void barcodeScanner() throws Exception {
        mockMvc.perform(get("/sales/scan/barcode/123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/scan/barcode/barcode"))
                .andExpect(model().attributeExists("barcode"));
    }
}