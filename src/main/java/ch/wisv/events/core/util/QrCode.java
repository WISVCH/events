package ch.wisv.events.core.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QrCode {
    public static BufferedImage generateQrCode(String text) throws WriterException {
        // Generate a QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 2 * 13 * 13, 2 * 13 * 13);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static byte[] bufferedImageToBytes(BufferedImage image) throws IOException {
        // Generate a QR code
        // Convert BufferedImage to PNG
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
