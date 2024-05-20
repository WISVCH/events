package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.utils.Barcode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SalesScanEventController.
 */
@Controller
@RequestMapping({"/sales/scan/barcode/{uniqueCode}","/sales/scan/barcode/{uniqueCode}/"})
@PreAuthorize("hasRole('USER')")
public class SalesScanBarcodeController {

    /** Extra barcode part length. */
    private static final int BARCODE_TICKET_LENGTH = 6;

    /** Ticket unique code allowed chars. */
    private static final String BARCODE_ALLOWED_CHARS = "0123456789";

    /**
     * View to scan a ticket/code for an event.
     *
     * @param model      of type Model
     * @param uniqueCode of type String
     *
     * @return String
     */
    @GetMapping
    public String barcodeScanner(Model model, @PathVariable String uniqueCode) {
        String barcode = RandomStringUtils.random(BARCODE_TICKET_LENGTH, BARCODE_ALLOWED_CHARS) + uniqueCode;
        barcode += Barcode.calculateChecksum(barcode.toCharArray());
        model.addAttribute("barcode", barcode);

        return "sales/scan/barcode/barcode";
    }
}
