package ch.wisv.events.core.service.mail;

import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.ticket.Ticket;
import java.util.List;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

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
            String subject = String.format("Ticket overview %s", order.getPublicReference().substring(0, ORDER_NUMBER_LENGTH));

            this.sendMailWithContent(order.getOwner().getEmail(), subject, this.templateEngine.process("mail/order", ctx));
        }
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
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper message;

        try {
            message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject("[CH Events] " + subject);
            message.setFrom("noreply@ch.tudelft.nl");
            message.setTo(recipientEmail);

            // Create the HTML body using Thymeleaf
            message.setText(content, true); // true = isHtml

            // Send mail
            this.mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailPreparationException("Unable to prepare email", e.getCause());
        } catch (MailException m) {
            throw new MailSendException("Unable to send email", m.getCause());
        }
    }
}
