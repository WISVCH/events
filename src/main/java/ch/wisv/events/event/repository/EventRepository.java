package ch.wisv.events.event.repository;

import ch.wisv.events.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Event repository.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    Event findById(Long orderId);

    List<Event> findByTitle(String title);

    List<Event> findByEndAfter(LocalDateTime start);

    Optional<Event> findByKey(String key);

    List<Event> findAllByProductsId(Long id);
//    List<Event> findByRegistrationStartBeforeAndRegistrationEndAfter(LocalDateTime registrationStart, LocalDateTime
//            registrationEnd);
}
