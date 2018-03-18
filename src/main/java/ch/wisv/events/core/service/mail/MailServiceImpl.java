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

@Service
public class MailServiceImpl implements MailService {

    /**
     * Field mailSender
     */
    private final JavaMailSender mailSender;

    /**
     * Field templateEngine
     */
    private final SpringTemplateEngine templateEngine;

    /**
     * Constructor MailServiceImpl creates a new MailServiceImpl instance.
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
     * Method send mail about Order to Customer.
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

    /**
     * Method mail Order to Customer.
     *
     * @param order of type Order
     */
    @Override
    public void sendOrderConfirmation(Order order, List<Ticket> tickets) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("order", order);
        ctx.setVariable("tickets", tickets);

        this.sendMailWithContent(order.getOwner().getEmail(), "Order overview", this.templateEngine.process("mail/order", ctx));
    }

    @Override
    public void sendErrorPaymentOrder(Order order) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("order", order);

        this.sendMailWithContent("w3cie@ch.tudelft.nl", "Order payment failed", this.templateEngine.process("mail/order-error", ctx));
    }

    @Override
    public void sendOrderReservation(Order order) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("order", order);

        this.sendMailWithContent(order.getOwner().getEmail(), "Ticket reservation", this.templateEngine.process("mail/order-reservation", ctx));
    }
}
