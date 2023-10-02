package ch.wisv.events.core.repository;

import ch.wisv.events.core.admin.Attendence;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ch.wisv.events.core.admin.TreasurerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            "select count(*) as ticketsCount, sum(status) as scannedCount, avg(status)*100 as percentageScanned " +
                    "from Ticket A INNER JOIN (Select distinct products_id FROM ticket T1 INNER JOIN (Select products_id from " +
                    "event_products EP INNER JOIN " +
                    "(Select id from event e where e.ending between :startDate and :endDate) E " +
                    "ON E.id=EP.event_id) T2 ON T1.product_id=T2.products_id) B " +
                    "ON A.product_id=B.products_id;", nativeQuery = true) //TODO fix proper date
    Attendence getAttendenceFromEventsInDateRange(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime End);

    @Query(value =
            "select count(*) as ticketsCount, coalesce(sum(status), 0) as scannedCount, coalesce(avg(status),0 ) * 100 as percentageScanned " +
                    "from Ticket A INNER JOIN (Select distinct products_id FROM ticket T1 INNER JOIN (Select products_id from " +
                    "event_products EP INNER JOIN " +
                    "(Select :event_id as id) E " +
                    "ON E.id=EP.event_id) T2 ON T1.product_id=T2.products_id) B " +
                    "ON A.product_id=B.products_id;", nativeQuery = true)
    Attendence getAttendanceFromEvent(@Param("event_id") Integer event_id);
}
