package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.event.Event;
import org.springframework.stereotype.Repository;

/**
 * EventRepository.
 */
@Repository
public interface EventRepository extends AbstractRepository<Event> {

}
