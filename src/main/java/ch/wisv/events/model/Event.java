package ch.wisv.events.model;

import com.google.common.base.MoreObjects;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Event entity.
 */
@Entity
public class Event {

    public final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    @Lob
    private String description;
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime start;
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
    }

    public Event(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(LocalDateTime registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public LocalDateTime getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(LocalDateTime registrationStart) {
        this.registrationStart = registrationStart;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public int getRegistrationLimit() {
        return registrationLimit;
    }

    public void setRegistrationLimit(int registrationLimit) {
        this.registrationLimit = registrationLimit;
    }

    public Set<Registration> getRegistrations() {
        return registrations;
    }

    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return registrationStart.isBefore(now) && registrationEnd.isAfter(now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id == event.id &&
                registrationLimit == event.registrationLimit &&
                Objects.equals(title, event.title) &&
                Objects.equals(description, event.description) &&
                Objects.equals(start, event.start) &&
                Objects.equals(end, event.end) &&
                Objects.equals(registrationStart, event.registrationStart) &&
                Objects.equals(registrationEnd, event.registrationEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, start, end, registrationStart, registrationEnd, registrationLimit);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("registrationLimit", registrationLimit)
                .add("registrationEnd", registrationEnd)
                .add("registrationStart", registrationStart)
                .add("end", end)
                .add("start", start)
                .add("description", description)
                .add("title", title)
                .add("id", id)
                .toString();
    }
}
