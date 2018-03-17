package ch.wisv.events.core.model.event;

import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.utils.LDAPGroup;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Event entity.
 */
@Entity
@Data
public class Event {

    /**
     * ID of the event, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Key of the event, getter only so it can not be changed.
     */
    @NotEmpty
    private String key;

    /**
     * Field title title of the Event.
     */
    @NotEmpty
    private String title;

    /**
     * Short description of the Event.
     */
    @NotEmpty
    private String shortDescription;

    /**
     * Field description description of the Event.
     */
    @Column(columnDefinition = "TEXT")
    @NotEmpty
    private String description;

    /**
     * Field location location of the Event.
     */
    private String location;

    /**
     * Field imageURL imageUrl to an image of the Event.
     */
    private String imageURL;

    /**
     * Product that are related to this event and can be sold. OneToMany so one Product can be used by one Event, but
     * an Event can contain multiple Products.
     */
    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    private List<Product> products;

    /**
     * Field start starting time of the Event.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @NotNull
    private LocalDateTime start;

    /**
     * Field ending ending time of the Event.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @NotNull
    private LocalDateTime ending;

    /**
     * Field target target of the amount of tickets sold by this Event.
     */
    @NotNull
    private Integer target;

    /**
     * Maximum number of products sold for the event. Its an Integer instead of an int, so the value can be null.
     */
    private Integer maxSold;

    /**
     * Publish status of the event.
     */
    private EventStatus published;

    /**
     * Commission/board which organizes the Event.
     */
    private LDAPGroup organizedBy;

    /**
     * List of all the possible catergories.
     */
    @NotNull
    @ElementCollection
    private List<EventCategory> categories;

    /**
     * Default constructor.
     */
    public Event() {
        this.key = UUID.randomUUID().toString();
        this.products = new ArrayList<>();
        this.published = EventStatus.NOT_PUBLISHED;
        this.organizedBy = LDAPGroup.BESTUUR;
        this.categories = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param title       Title of the Event
     * @param description Description of the Event
     * @param location    Location of the Event
     * @param target      Target of the Event
     * @param maxSold     Limit of the Event
     * @param imageURL    Path to the Image of the Event
     * @param start       Starting DateTime of the Event
     * @param ending      Ending DateTime of the Event
     */
    public Event(
            String title,
            String description,
            String location,
            int target,
            Integer maxSold,
            String imageURL,
            LocalDateTime start,
            LocalDateTime ending,
            String shortDescription
    ) {
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.target = target;
        this.maxSold = maxSold;
        this.start = start;
        this.ending = ending;
        this.imageURL = imageURL;
        this.shortDescription = shortDescription;
    }

    /**
     * Add product to the event.
     *
     * @param product product to be added to the event
     */
    public void addProduct(Product product) {
        this.products.add(product);
    }

    /**
     * Calculate the progress of the products sold and the target of the event and round number to
     * two decimals.
     *
     * @return progress of event
     */
    public double calcSoldProgress() {
        return this.calcProgress(this.getSold());
    }

    public double calcReservedProcess() {
        return this.calcProgress(this.getReserved());
    }

    private double calcProgress(double reserved) {
        return Math.min(Math.round(((reserved / (double) this.target) * 100.d) * 100.d) / 100.d, 100.d);
    }

    public int getSold() {
        return products.stream().mapToInt(Product::getSold).sum();
    }

    public int getReserved() {
        return products.stream().mapToInt(Product::getReserved).sum();
    }
}
