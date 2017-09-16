package ch.wisv.events.core.service.mail;

import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    public void sendOrderToCustomer(Order order, List<SoldProduct> soldProductList) {
        final Context ctx = new Context(new Locale("en"));
        ctx.setVariable("order", order);
        ctx.setVariable("soldProducts", soldProductList);

        this.sendMailWithContent(
                order.getCustomer().getEmail(),
                "Order overview",
                this.templateEngine.process("mail/order", ctx)
        );
    }
}
