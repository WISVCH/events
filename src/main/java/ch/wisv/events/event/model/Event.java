package ch.wisv.events.event.model;

import ch.wisv.events.event.legacy.Registration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Event entity.
 */
@Entity
@Data
public class Event {

    public final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @Lob
    private String description;

    @OneToMany(mappedBy = "event")
    private Set<Ticket> tickets;

    @Column(name = "eventStart")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime start;

    @Column(name = "eventEnd")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime end;

    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime registrationStart;

    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime registrationEnd;
    private int registrationLimit;

    @OneToMany(mappedBy = "event")
    private Set<Registration> registrations;

    public Event() {
        this.tickets = new HashSet<>();
    }
    public Event(String title) {
        this.title = title;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

}
