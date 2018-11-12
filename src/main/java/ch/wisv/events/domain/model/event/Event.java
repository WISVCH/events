package ch.wisv.events.domain.model.event;

import ch.wisv.events.domain.converter.ZonedDateTimeConverter;
import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.user.LdapGroup;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
    @Convert(converter = ZonedDateTimeConverter.class)
    @NotNull(message = "Starting date time can not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime starting;

    /**
     * Ending DateTime of the Event.
     */
    @Convert(converter = ZonedDateTimeConverter.class)
    @NotNull(message = "Ending date time can not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
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
    private List<EventCategory> categories = new ArrayList<>();

    /**
     * Feature image of the Event.
     */
    private String image;

    /**
     * Products of the Event.
     */
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    @OrderBy(value = "price ASC")
    private List<Product> products = new ArrayList<>();

    /**
     * Event constructor.
     */
    public Event() {
        super();
        this.status = EventStatus.NOT_PUBLISHED;
    }
}
