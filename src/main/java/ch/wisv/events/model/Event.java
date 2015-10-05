package ch.wisv.events.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Event entity.
 */
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    @Lob
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.ALL})
    private Set<Registration> registrations;

    protected Event() {
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

    public Set<Registration> getRegistrations() {
        return registrations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id == event.id &&
                Objects.equals(title, event.title) &&
                Objects.equals(description, event.description) &&
                Objects.equals(start, event.start) &&
                Objects.equals(end, event.end) &&
                Objects.equals(registrationStart, event.registrationStart) &&
                Objects.equals(registrationEnd, event.registrationEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, start, end, registrationStart, registrationEnd);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("title", title)
                .add("description", description)
                .add("start", start)
                .add("end", end)
                .add("registrationStart", registrationStart)
                .add("registrationEnd", registrationEnd)
                .toString();
    }
}
