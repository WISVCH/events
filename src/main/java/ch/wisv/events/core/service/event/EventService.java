package ch.wisv.events.core.service.event;

import ch.wisv.events.core.model.event.Event;

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
     * Delete a product from an Event
     *
     * @param eventId   eventId
     * @param productId productId
     */
    void deleteProductFromEvent(Integer eventId, Integer productId);

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
     * @param key key of an Product
     * @return List of Events
     */
    List<Event> getEventByProductKey(String key);

    /**
     * Method soldFivePrevious returns the fivePrevious of this EventService object.
     *
     * @return the fivePrevious (type List<Event>) of this EventService object.
     */
    List<Event> soldFivePrevious();

    /**
     * Method soldFiveUpcoming returns the fiveUpcoming of this EventService object.
     *
     * @return the fiveUpcoming (type List<Event>) of this EventService object.
     */
    List<Event> soldFiveUpcoming();

}
