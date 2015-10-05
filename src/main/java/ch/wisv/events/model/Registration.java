package ch.wisv.events.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
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
public class Registration {
    @Id
    @ManyToOne
    private Person person;
    @Id
    @ManyToOne
    private Event event;
    private LocalDateTime date;

    protected Registration() {
    }

    public Registration(Person person, Event event, LocalDateTime date) {
        this.person = person;
        this.event = event;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Registration)) return false;
        Registration that = (Registration) o;
        return Objects.equals(person, that.person) &&
                Objects.equals(event, that.event) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, event, date);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("person", person)
                .add("event", event)
                .add("date", date)
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
