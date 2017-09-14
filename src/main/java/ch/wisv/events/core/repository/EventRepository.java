package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventOptions;
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
     * Find Events that ending after a certain dateTime, so all upcoming events.
     *
     * @param dateTime DateTime an event should ending after
     * @return list of Events
     */
    List<Event> findByEndingAfter(LocalDateTime dateTime);

    /**
     * Find all Events that are organized by a certain LDAPGroup.
     *
     * @param options of type EventOptions
     * @return List<Event>
     */
    List<Event> findAllByOptions(EventOptions options);

    /**
     * Find an Event by key
     *
     * @param key key of an Event
     * @return list of Events
     */
    Optional<Event> findByKey(String key);
}
