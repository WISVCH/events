package ch.wisv.events.service.event;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventOptionsRequest;
import ch.wisv.events.data.request.event.EventProductRequest;
import ch.wisv.events.data.request.event.EventRequest;

import java.util.Collection;
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
    Collection<Event> getAllEvents();

    /**
     * Get all upcoming Events
     *
     * @return Collection of Events
     */
    Collection<Event> getUpcomingEvents();

    /**
     * Get Event by ID
     *
     * @param id id of an Event
     * @return Event
     */
    Event getEventById(Long id);

    /**
     * Get Event by key
     *
     * @param key key of an Event
     * @return Event
     */
    Event getEventByKey(String key);

    /**
     * Add a new Event by a EventRequest
     *
     * @param eventRequest EventRequest
     */
    Event addEvent(EventRequest eventRequest);

    /**
     * Add a product to an Event
     *
     * @param eventProductRequest EventProductRequest
     */
    void addProductToEvent(EventProductRequest eventProductRequest);

    /**
     * Delete a product from an Event
     *
     * @param eventId   eventId
     * @param productId productId
     */
    void deleteProductFromEvent(Long eventId, Long productId);

    /**
     * Update an Event by an EventRequest
     *
     * @param eventRequest EventRequest
     */
    void updateEvent(EventRequest eventRequest);

    /**
     * Delete an Event
     *
     * @param event Event
     */
    void deleteEvent(Event event);

    /**
     * Update the EventOptions of an Event
     *
     * @param request EventOptionsRequest
     */
    void updateEventOptions(EventOptionsRequest request);

    /**
     * Get all Events that are connected to the same Product
     *
     * @param key key of an Product
     * @return List of Events
     */
    List<Event> getEventByProductKey(String key);

}
