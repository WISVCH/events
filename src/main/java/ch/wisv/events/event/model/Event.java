package ch.wisv.events.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Event entity.
 */
@Entity
@Data
@AllArgsConstructor
public class Event {

    public final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue
    public long id;

    public String key;

    public String title;

    @Lob
    public String description;

    public String location;

    public String imageURL;

    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Ticket.class, fetch = FetchType.EAGER)
    public Set<Ticket> tickets;

    @DateTimeFormat(pattern = TIME_FORMAT)
    public LocalDateTime start;

    @DateTimeFormat(pattern = TIME_FORMAT)
    public LocalDateTime end;

    public int registrationLimit;

    public Event() {
        this.tickets = new HashSet<>();
        this.key = UUID.randomUUID().toString();
    }

    public Event(String title) {
        this();
        this.title = title;
    }

    public Event(String title, String description, String location, int limit, LocalDateTime start, LocalDateTime
            end, String imageURL) {
        this(title);
        this.description = description;
        this.location = location;
        this.registrationLimit = limit;
        this.start = start;
        this.end = end;
        this.imageURL = imageURL;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

}
