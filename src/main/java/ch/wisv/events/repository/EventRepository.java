package ch.wisv.events.repository;

import ch.wisv.events.model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Event repository.
 */
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findByTitle(String title);
}
