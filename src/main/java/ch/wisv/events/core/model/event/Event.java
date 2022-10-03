package ch.wisv.events.core.model.event;

import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.utils.LdapGroup;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
    private String imageUrl;

    /**
     * Product that are related to this event and can be sold. OneToMany so one Product can be used by one Event, but
     * an Event can contain multiple Products.
     */
    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    @OrderBy(value = "cost ASC")
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
     * Field target target of the amount of webshop sold by this Event.
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
    private LdapGroup organizedBy;

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
        this.organizedBy = LdapGroup.BESTUUR;
        this.categories = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param title            Title of the Event
     * @param description      Description of the Event
     * @param location         Location of the Event
     * @param target           Target of the Event
     * @param maxSold          Limit of the Event
     * @param imageUrl         Path to the Image of the Event
     * @param start            Starting DateTime of the Event
     * @param ending           Ending DateTime of the Event
     * @param shortDescription of type String
     */
    public Event(
            String title,
            String description,
            String location,
            int target,
            Integer maxSold,
            String imageUrl,
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
        this.imageUrl = imageUrl;
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
     * @return double
     */
    public double calcSoldProgress() {
        return this.calcProgress(this.getSold());
    }

    /**
     * Calculate the progress of the products reserved and the target of the event and round number to
     * two decimals.
     *
     * @return double
     */
    public double calcReservedProcess() {
        return this.calcProgress(this.getReserved());
    }

    /**
     * Get amount of tickets sold for this event.
     *
     * @return int
     */
    public int getSold() {
        return products.stream().mapToInt(Product::getSold).sum();
    }

    /**
     * Get amount of tickets reserved for this event.
     *
     * @return int
     */
    public int getReserved() {
        return products.stream().mapToInt(Product::getReserved).sum();
    }

    /**
     * Calculate the progress of target.
     *
     * @param reserved of type double
     *
     * @return double
     */
    private double calcProgress(double reserved) {
        return Math.min(Math.round(((reserved / (double) this.target) * 100.d) * 100.d) / 100.d, 100.d);
    }

    /**
     * Check if the event is sold out.
     *
     * @return boolean
     */
    public boolean isSoldOut() {
        return this.maxSold != null && this.getSold() >= this.maxSold;
    }
}
