package ch.wisv.events.event.repository;

import ch.wisv.events.event.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by sven on 12/10/2016.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findById(Long orderId);

    Ticket findByKey(String ticketKey);

}
