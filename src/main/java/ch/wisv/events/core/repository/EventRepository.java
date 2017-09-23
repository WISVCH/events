package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.utils.LDAPGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Event repository.
 */
public interface EventRepository extends JpaRepository<Event, Integer> {

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
     * @param published   of type EventStatus
     * @param organizedBy of type LDAPGroup
     * @return List<Event>
     */
    List<Event> findAllByPublishedAndOrganizedByAndEndingIsAfter(EventStatus published, LDAPGroup organizedBy, LocalDateTime ending);

    /**
     * Method findAllByPublished ...
     *
     * @param published of type EventStatus
     * @return List<Event>
     */
    List<Event> findAllByPublishedAndEndingIsAfter(EventStatus published, LocalDateTime ending);

    /**
     * Find an Event by key
     *
     * @param key key of an Event
     * @return list of Events
     */
    Optional<Event> findByKey(String key);
}
