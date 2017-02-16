package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Event repository.
 */
public interface EventRepository extends JpaRepository<Event, Integer> {

    /**
     * Find an Event by ID.
     *
     * @param id id of Event
     * @return Event
     */
    Event findById(Integer id);

    /**
     * Find Events that end after a certain dateTime, so all upcoming events
     *
     * @param dateTime DateTime an event should end after
     * @return list of Events
     */
    List<Event> findByEndAfter(LocalDateTime dateTime);

    /**
     * Find an Event by key
     *
     * @param key key of an Event
     * @return list of Events
     */
    Optional<Event> findByKey(String key);

    /**
     * Find all events that are connect to the same Product by Product ID
     *
     * @param id id of a Product
     * @return list of Events
     */
    List<Event> findAllByProductsId(Integer id);


    /**
     * Method findTop5ByEndAfterOrderByEnd ...
     *
     * @param dateTime of type LocalDateTime
     * @return List<Event>
     */
    List<Event> findTop5ByEndAfterOrderByEnd(LocalDateTime dateTime);


    /**
     * Method findTop5ByEndBeforeOrderByEndDesc ...
     *
     * @param dateTime of type LocalDateTime
     * @return List<Event>
     */
    List<Event> findTop5ByEndBeforeOrderByEndDesc(LocalDateTime dateTime);

}
