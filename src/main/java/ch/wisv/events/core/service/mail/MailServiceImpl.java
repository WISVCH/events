package ch.wisv.events.core.service.mail;

import biweekly.util.IOUtils;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.ticket.Ticket;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import ch.wisv.events.core.util.QrCode;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * MailService implementation.
 */
@Service
public class MailServiceImpl implements MailService {

    /** Order number length. */
    private static final int ORDER_NUMBER_LENGTH = 8;

    /** JavaMailSender. */
    private final JavaMailSender mailSender;

    /** SpringTemplateEngine. */
    private final SpringTemplateEngine templateEngine;

    /**
     * MailServiceImpl constructor.
     *
     * @param mailSender     of type JavaMailSender
     * @param templateEngine of type templateEngine
     */
    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Method mail Order to Customer.
     *
     * @param order of type Order
     */
    @Override
    public void sendOrderConfirmation(Order order, List<Ticket> tickets) {
        if (tickets.size() > 0) {
            final Context ctx = new Context(new Locale("en"));
            ctx.setVariable("order", order);
            ctx.setVariable("tickets", tickets);
            ctx.setVariable("redirectLinks", tickets.stream().anyMatch(ticket -> ticket.getProduct().getRedirectUrl() != null));
            String subject = String.format("Ticket overview %s", order.getPublicReference().substring(0, ORDER_NUMBER_LENGTH));

            this.sendMailWithContent(order.getOwner().getEmail(), subject, this.templateEngine.process("mail/order", ctx), tickets);
        }
    }



    /**
     * Method mail transferred ticket to Customer.
     * @param ticket of type Ticket
     * @param oldCustomer of type Customer
     * @param newCustomer of type Customer
     */
    @Override
    public void sendTransferConfirmation(Ticket ticket, Customer oldCustomer, Customer newCustomer) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("ticket", ticket);
        ctx.setVariable("oldCustomer", oldCustomer);
        ctx.setVariable("oldCustomer", oldCustomer);
        ctx.setVariable("redirectLink", ticket.getProduct().getRedirectUrl());
        String subject = String.format("Ticket transfer %s", ticket.getProduct().getTitle());

        List<Ticket> tickets = List.of(ticket);
        this.sendMailWithContent(newCustomer.getEmail(), subject, this.templateEngine.process("mail/transfer", ctx), tickets);
    }

    /**
     * Method mails about a error.
     *
     * @param subject   of type String
     * @param exception of type Exception
     */
    @Override
    public void sendError(String subject, Exception exception) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("exception", exception);

        this.sendMailWithContent("w3cie@ch.tudelft.nl", subject, this.templateEngine.process("mail/error", ctx));
    }

    /**
     * Send an Order Reservation mail.
     *
     * @param order of type Order
     */
    @Override
    public void sendOrderReservation(Order order) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("order", order);
        String subject = String.format("Ticket reservation %s", order.getPublicReference().substring(0, ORDER_NUMBER_LENGTH));

        this.sendMailWithContent(order.getOwner().getEmail(), subject, this.templateEngine.process("mail/order-reservation", ctx));
    }

    /**
     * Method send mail about Order to Customer.
     *
     * @param recipientEmail of type String
     * @param subject        of type String
     * @param content        of type String
     */
    private void sendMailWithContent(String recipientEmail, String subject, String content) {
        sendMailWithContent(recipientEmail, subject, content, null);
    }

    /**
     * Method send mail about Order to Customer.
     *
     * @param recipientEmail of type String
     * @param subject        of type String
     * @param content        of type String
     * @param uniqueCodes       list of type String
     */
    private void sendMailWithContent(String recipientEmail, String subject, String content, List<Ticket> tickets) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper message;

        try {
            message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");
            message.setSubject("[CH Events] " + subject);
            message.setFrom("noreply@ch.tudelft.nl");
            message.setTo(recipientEmail);

            // Create the HTML body using Thymeleaf
            message.setText(content, true); // true = isHtml
            message.addInline("ch-logo.png", new ClassPathResource("/static/images/ch-logo.png"), "image/png");

            if(tickets != null) {
                for (Ticket ticket : tickets) {
                    String uniqueCode = ticket.getUniqueCode();
                    // Retrieve and return barcode (LEGACY)
                    if (uniqueCode.length() == 6){
                        // Get barcode from url
                        String url = "https://barcode.tec-it.com/barcode.ashx?data=978020" + uniqueCode + "&code=EAN13&multiplebarcodes=false&translate-esc=false&unit=Fit&dpi=96&imagetype=Gif&rotation=0&color=%23000000&bgcolor=%23FFFFFF&qunit=Mm&quiet=0";
                        URL urlObj = new URL(url);
                        InputStream is = urlObj.openStream();
                        byte[] bytes = IOUtils.toByteArray(is);
    
                        // Attach image inline to message
                        message.addInline("ch-" + uniqueCode + ".png", new ByteArrayResource(bytes), "image/png");
                    } else {
                        BufferedImage qrCode = QrCode.generateQrCode(uniqueCode);
                        byte[] bytes = QrCode.bufferedImageToBytes(qrCode);
                        message.addInline("ch-" + uniqueCode + ".png", new ByteArrayResource(bytes), "image/png");
                    }
                }
            }

            // Send mail
            this.mailSender.send(mimeMessage);
        } catch (MessagingException | IOException | WriterException  e) {
            throw new MailPreparationException("Unable to prepare email", e.getCause());
        } catch (MailException m) {
            throw new MailSendException("Unable to send email", m.getCause());
        }
    }


}
