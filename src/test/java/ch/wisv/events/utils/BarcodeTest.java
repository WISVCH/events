package ch.wisv.events.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BarcodeTest {

    @Test
    public void testIsValidEanCodeTrue() {
        String barcode = "123456000000";
        String barcodePlusCheckDigit = "1234560000005";

        assertEquals(5, Barcode.calculateChecksum(barcode.toCharArray()));
        assertTrue(Barcode.isValidEanCode(barcodePlusCheckDigit));
    }

    @Test
    public void testIsValidEanCodeFalse() {
        String barcode = "123456000000";
        String barcodePlusCheckDigit = "1234560000006";

        assertEquals(5, Barcode.calculateChecksum(barcode.toCharArray()));
        assertFalse(Barcode.isValidEanCode(barcode));
    }

}