package ch.wisv.events.core.service.event;

import ch.wisv.events.core.exception.normal.EventInvalidException;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.document.Document;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EventService.
 */
public interface EventService {

    /**
     * Get all Events.
     *
     * @return Collection of Events
     */
    List<Event> getAll();

    /**
     * Get the count of all events.
     *
     * @return the eventcount
     */
    long count();

    /**
     * Get all Events between a lowerBound and upperBound.
     *
     * @param lowerBound of type LocalDateTime
     * @param upperBound of type LocalDateTime
     *
     * @return List of Events
     */
    List<Event> getAllBetween(LocalDateTime lowerBound, LocalDateTime upperBound);

    /**
     * Get all upcoming Events.
     *
     * @return List of Events
     */
    List<Event> getUpcoming();

    /**
     * Get Event by key.
     *
     * @param key key of an Event
     *
     * @return Event
     *
     * @throws EventNotFoundException when Event is not found
     */
    Event getByKey(String key) throws EventNotFoundException;

    /**
     * Get all Events that are connected to the same Product.
     *
     * @param product of type Product
     *
     * @return List of Events
     *
     * @throws EventNotFoundException when Event is not found
     */
    Event getByProduct(Product product) throws EventNotFoundException;

    /**
     * Method getPreviousEventsLastTwoWeeks returns the previousEventsLastTwoWeeks of this EventService object.
     *
     * @return List of Events
     */
    List<Event> getPreviousEventsLastTwoWeeks();

    /**
     * Add a new Event by a EventRequest.
     *
     * @param event Event
     *
     * @throws EventInvalidException when Event is invalid
     */
    void create(Event event) throws EventInvalidException;

    /**
     * Update event by Event.
     *
     * @param event Event
     *
     * @throws EventNotFoundException when Event is not found
     * @throws EventInvalidException  when Event is invalid
     */
    void update(Event event) throws EventNotFoundException, EventInvalidException;

    /**
     * Delete an Event.
     *
     * @param event Event
     */
    void delete(Event event);

    /**
     * Add document image to Event.
     *
     * @param event    of type Event
     * @param document of type Document
     */
    void addDocumentImage(Event event, Document document);

}
