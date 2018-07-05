package ch.wisv.events.utils;

import java.util.Arrays;

/**
 * Barcode helper class.
 */
public final class Barcode {

    /** In ASCII code, the numbers (digits) start from 48. */
    private static final int ASCII_START_DIGITS = 48;

    /** Number of digits in an EAN 13 barcode. */
    private static final int LENGTH_BARCODE_EAN_13 = 13;

    /**
     * Utility classes should not have a public or default constructor.
     */
    private Barcode() {
    }

    /**
     * Is barcode a valid EanCode.
     *
     * @param barcode of type String
     *
     * @return bool
     */
    public static boolean isValidEanCode(String barcode) {
        char[] chars = barcode.toCharArray();

        if (chars.length != LENGTH_BARCODE_EAN_13) {
            return false;
        }

        int checkDigit = chars[chars.length - 1] - ASCII_START_DIGITS;
        int checksum = calculateChecksum(Arrays.copyOfRange(chars, 0, chars.length - 1));

        return checkDigit == checksum;
    }

    /**
     * Calculate the checksum of a char[].
     *
     * @param chars of char[]
     *
     * @return int
     */
    public static int calculateChecksum(char[] chars) {
        int evenCount = 0;
        int oddCount = 0;

        for (int i = 0; i < chars.length; i++) {
            int digit = chars[chars.length - (i + 1)] - ASCII_START_DIGITS;

            if (((i + 1) % 2) == 0) {
                evenCount += digit;
            } else {
                oddCount += digit;
            }
        }

        return (10 - ((3 * oddCount + evenCount) % 10)) % 10;
    }
}
