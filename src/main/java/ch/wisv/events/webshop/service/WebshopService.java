package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import java.util.List;

/**
 * WebshopService class.
 */
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
     * @throws EventsException when the Order is invalid
     */
    void updateOrderStatus(Order order, String paymentsReference) throws EventsException;

    /**
     * Fetch the status of the Order via the Payments API.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     *
     * @throws EventsException when CH Payment status is unknown or the Order is invalid
     */
    void fetchOrderStatus(Order order, String paymentsReference) throws EventsException;
}
