package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * EventRepository interface.
 */
public interface EventRepository extends JpaRepository<Event, Integer> {

    /**
     * Find Events that ending after a certain dateTime, so all upcoming events.
     *
     * @param dateTime DateTime an event should ending after
     *
     * @return list of Events
     */
    List<Event> findByEndingAfter(LocalDateTime dateTime);

    /**
     * Method findAllByPublished ...
     *
     * @param published of type EventStatus
     *
     * @return List
     */
    List<Event> findAllByPublishedAndEndingIsAfter(EventStatus published, LocalDateTime ending);

    /**
     * Find an Event by key.
     *
     * @param key key of an Event
     *
     * @return list of Events
     */
    Optional<Event> findByKey(String key);

    /**
     * Find all Event with ending between a period of time.
     *
     * @param start  of type LocalDateTime
     * @param ending of type LocalDateTime
     *
     * @return List
     */
    List<Event> findAllByEndingBetween(LocalDateTime start, LocalDateTime ending);

    /**
     * Find all Event with start between a period of time.
     *
     * @param after  of type LocalDateTime
     * @param before of type LocalDateTime
     *
     * @return List
     */
    List<Event> findAllByStartIsAfterAndStartIsBefore(LocalDateTime after, LocalDateTime before);

    /**
     * Method findByProductsContaining ...
     *
     * @param product of type Product
     *
     * @return Optional
     */
    Optional<Event> findByProductsContaining(Product product);
}
