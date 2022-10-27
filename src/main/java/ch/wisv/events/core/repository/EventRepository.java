package ch.wisv.events.core.repository;

import ch.wisv.events.core.admin.Attendence;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ch.wisv.events.core.admin.TreasurerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<Event> findByEndingAfterOrderByStartAsc(LocalDateTime dateTime);

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
     * @return the count of all
     */
    long countAllByStartIsAfterAndStartIsBefore(LocalDateTime after, LocalDateTime before);

    /**
     * Find all Event with start between a period of time.
     *
     * @param after  of type LocalDateTime
     * @param before of type LocalDateTime
     *
     * @return List of Events
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

    @Query(value =
            "select count(*) as ticketsCount, avg(status)*100 as percentageScanned " +
                    "from Ticket A INNER JOIN (Select distinct products_id FROM ticket T1 INNER JOIN (Select products_id from " +
                    "event_products EP INNER JOIN " +
                    "(Select id from event e where e.start > ?1 and e.ending < ?2) E " +
                    "ON E.id=EP.event_id) T2 ON T1.product_id=T2.products_id WHERE status=1) B " +
                    "ON A.product_id=B.products_id;", nativeQuery = true)
    Attendence getAttendenceFromEventsInDateRange(LocalDateTime start, LocalDateTime End);
}
