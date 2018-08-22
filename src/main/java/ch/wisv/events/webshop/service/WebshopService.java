package ch.wisv.events.webshop.service;

import ch.wisv.events.core.model.event.Event;
import java.util.List;

/**
 * WebshopService class.
 */
public interface WebshopService {

    /**
     * Filter the products in a Event which are not sold now.
     *
     * @param event of type Event
     *
     * @return Event
     */
    Event filterEventProductNotSalable(Event event);

    /**
     * Filter the products by events if they can be sold or not.
     *
     * @param events of type List
     *
     * @return List
     */
    List<Event> filterEventProductNotSalable(List<Event> events);
}
