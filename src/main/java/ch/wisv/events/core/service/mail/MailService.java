package ch.wisv.events.core.service.mail;

import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.ticket.Ticket;
import java.util.List;

public interface MailService {

    /**
     * Method mail Order confirmation to Customer.
     *
     * @param order   of type Order
     * @param tickets of type List<Ticket>
     */
    void sendOrderConfirmation(Order order, List<Ticket> tickets);

    void sendErrorPaymentOrder(Order order);

    void sendOrderReservation(Order order);
}
