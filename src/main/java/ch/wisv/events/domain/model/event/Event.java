package ch.wisv.events.domain.model.event;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.customer.LdapGroup;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Event entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Event extends AbstractModel {

    /**
     * Max length in 255 characters of the short description.
     */
    private static final int MAX_LENGTH_SHORT_DESCRIPTION = 255;

    /**
     * Title of the Event.
     */
    @NotEmpty(message = "Title can not be empty")
    private String title;

    /**
     * Short description of the Event in 255 chars.
     */
    @NotEmpty(message = "Short description can not be empty")
    @Size(max = MAX_LENGTH_SHORT_DESCRIPTION, message = "Max length of the short description is 255 characters")
    private String shortDescription;

    /**
     * Description of the Event.
     */
    @Column(columnDefinition = "TEXT")
    @NotEmpty(message = "Description can not be empty")
    private String description;

    /**
     * Location of the Event.
     */
    @NotEmpty(message = "Location can not be empty")
    private String location;

    /**
     * Starting DateTime of the Event.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "Starting date time can not be null")
    private ZonedDateTime starting;

    /**
     * Ending DateTime of the Event.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "Ending date time can not be null")
    private ZonedDateTime ending;

    /**
     * Ticket sold target for the Event.
     */
    @Min(value = 1, message = "Target should be bigger than zero")
    private int ticketTarget;

    /**
     * Max sold tickets for the Event.
     */
    @Min(value = 0, message = "Limit should be bigger than zero or zero for no limit")
    private int ticketLimit;

    /**
     * The committee who is organizing the Event.
     */
    @NotNull(message = "Select a committee who is organizing the event")
    private LdapGroup organizedBy;

    /**
     * The status of the Event.
     */
    @NotNull(message = "Status cannot be null")
    private EventStatus status;

    /**
     * List of categories of the Event.
     */
    @ElementCollection
    @NotEmpty(message = "Select at least one category")
    private List<EventCategory> categories;

    /**
     * Feature image of the Event.
     */
    private String image;

    /**
     * Event constructor.
     */
    public Event() {
        super();
        this.status = EventStatus.NOT_PUBLISHED;
        this.categories = new ArrayList<>();
    }
}
