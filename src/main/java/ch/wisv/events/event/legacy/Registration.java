package ch.wisv.events.event.legacy;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.user.model.Person;
import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Event registration.
 * <p>
 * Provides Person-Event Many-to-Many with extra columns
 */
@Entity
@IdClass(Registration.RegistrationId.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "code"}))
public class Registration {
    @Id
    @ManyToOne
    private Person person;
    @Id
    @ManyToOne
    private Event event;
    private LocalDateTime date;
    @Column(length = 4)
    private String code;

    protected Registration() {
    }

    public Registration(Person person, Event event, LocalDateTime date, String code) {
        this.person = person;
        this.event = event;
        this.date = date;
        this.code = code;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Registration)) return false;
        Registration that = (Registration) o;
        return Objects.equals(person, that.person) &&
                Objects.equals(event, that.event) &&
                Objects.equals(date, that.date) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, event, date, code);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("person", person)
                .add("event", event)
                .add("date", date)
                .add("code", code)
                .toString();
    }

    protected static class RegistrationId implements Serializable {
        private long person;
        private long event;

        protected RegistrationId() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RegistrationId)) return false;
            RegistrationId that = (RegistrationId) o;
            return event == that.event &&
                    Objects.equals(person, that.person);
        }

        @Override
        public int hashCode() {
            return Objects.hash(person, event);
        }
    }
}
