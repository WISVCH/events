package ch.wisv.events.core.model.event;

import ch.wisv.events.core.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Event entity.
 */
@Entity
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Event {

    /**
     * Date format.
     */
    private final String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * ID of the event, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Key of the event, getter only so it can not be changed.
     */
    private String key;

    /**
     * Field title title of the Event.
     */
    private String title;

    /**
     * Field description description of the Event.
     */
    @Column(columnDefinition="TEXT")
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
    private LocalDateTime start;

    /**
     * Field ending ending time of the Event.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime ending;

    /**
     * Field sold amount of tickets sold by this Event.
     */
    private int sold;

    /**
     * Field target target of the amount of tickets sold by this Event.
     */
    private int target;

    /**
     * Maximum number of products sold for the event. Its an Integer instead of an int, so the value can be null.
     */
    private Integer maxSold;

    /**
     * Options for the event.
     */
    private EventOptions options;

    /**
     * Default constructor.
     */
    public Event() {
        this.key = UUID.randomUUID().toString();
        this.products = new ArrayList<>();
        this.options = new EventOptions();
    }

    /**
     * Constructor.
     *
     * @param title       Title of the Event
     * @param description Description of the Event
     * @param location    Location of the Event
     * @param target      Target of the Event
     * @param maxSold       Limit of the Event
     * @param imageURL    Path to the Image of the Event
     * @param start       Starting DateTime of the Event
     * @param ending         Ending DateTime of the Event
     */
    public Event(String title, String description, String location, int target, Integer maxSold, String imageURL,
                 LocalDateTime start, LocalDateTime ending) {
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.target = target;
        this.maxSold = maxSold;
        this.start = start;
        this.ending = ending;
        this.imageURL = imageURL;
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
    public double calcProgress() {
        return Math.min(Math.round((((double) this.sold / (double) this.target) * 100.d) * 100.d) / 100.d, 100.d);
    }

}
