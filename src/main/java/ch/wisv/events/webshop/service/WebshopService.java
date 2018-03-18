package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import java.util.List;

public interface WebshopService {

    /**
     * Filter the products by events if they can be sold or not.
     *
     * @param events of type List
     *
     * @return List
     */
    List<Event> filterNotSalableProducts(List<Event> events);

    /**
     * Update the status of the Order via the Payments API.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     *
     * @throws PaymentsStatusUnknown when CH Payment status is unknown
     * @throws OrderInvalidException when an Order is invalid
     */
    void updateOrderStatus(Order order, String paymentsReference) throws PaymentsStatusUnknown, OrderInvalidException;
}
