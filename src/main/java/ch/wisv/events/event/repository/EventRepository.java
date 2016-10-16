package ch.wisv.events.event.repository;

import ch.wisv.events.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event repository.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findById(Long orderId);

    List<Event> findByTitle(String title);

    List<Event> findByEndAfter(LocalDateTime start);

    Event findByKey(String key);

//    List<Event> findByRegistrationStartBeforeAndRegistrationEndAfter(LocalDateTime registrationStart, LocalDateTime
//            registrationEnd);
}
