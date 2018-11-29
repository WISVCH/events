package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.event.Event;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * EventRepository.
 */
@Repository
public interface EventRepository extends AbstractRepository<Event> {

    /**
     * Find all by starting time of the Event.
     *
     * @param starting of type ZonedDateTime
     *
     * @return List<Event>
     */
    List<Event> findByStartingAfterOrderByStartingAsc(ZonedDateTime starting);

}
