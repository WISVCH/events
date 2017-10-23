package ch.wisv.events.core.service.event;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EventService.
 */
public interface EventService {

    /**
     * Get all Events
     *
     * @return Collection of Events
     */
    List<Event> getAllEvents();

    /**
     * Get all Events between a lowerbound and upperbound
     *
     * @param lowerbound of type LocalDateTime
     * @param upperbound of type LocalDateTime
     * @return List<Event>
     */
    List<Event> getAllEventsBetween(LocalDateTime lowerbound, LocalDateTime upperbound);

    /**
     * Get all upcoming Events
     *
     * @return Collection of Events
     */
    List<Event> getUpcomingEvents();

    /**
     * Get all available events
     *
     * @return Collection of Events
     */
    List<Event> getAvailableEvents();

    /**
     * Get Event by key
     *
     * @param key key of an Event
     * @return Event
     */
    Event getByKey(String key);

    /**
     * Add a new Event by a EventRequest
     *
     * @param event Event
     */
    void create(Event event);

    /**
     * Update event by Event
     *
     * @param event Event
     */
    void update(Event event);

    /**
     * Delete an Event
     *
     * @param event Event
     */
    void delete(Event event);

    /**
     * Get all Events that are connected to the same Product
     *
     * @param product of type Product
     * @return List of Events
     */
    Event getEventByProduct(Product product);

    /**
     * Method getPreviousEventsLastTwoWeeks returns the previousEventsLastTwoWeeks of this EventService object.
     *
     * @return the previousEventsLastTwoWeeks (type List<Event>) of this EventService object.
     */
    List<Event> getPreviousEventsLastTwoWeeks();
}
