package ch.wisv.events.data.model.event;

import ch.wisv.events.data.model.Model;
import ch.wisv.events.data.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Event entity
 */
@Entity
@AllArgsConstructor
public class Event implements Model {

    /**
     * Date format
     */
    private final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * ID of the event, getter only so it can not be changed
     */
    @Id
    @GeneratedValue
    @Getter
    private long id;

    /**
     * Key of the event, getter only so it can not be changed
     */
    @Getter
    private String key;

    /**
     * Title of the event,
     * Description of the event,
     * Location of the event,
     * ImageURL of the event (path to the image);
     */
    @Getter
    @Setter
    private String title, description, location, imageURL;

    /**
     * Product that are related to this event and can be sold.
     * OneToMany so one Product can be used by one Event, but an Event
     * can contain multiple Products.
     */
    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Product> products;

    /**
     * Start and ending time of the Event.
     */
    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    private LocalDateTime start, end;

    /**
     * Amount of products sold and the target of the event.
     */
    @Getter
    @Setter
    private int sold, target;

    /**
     * Maximum number of products sold for the event. Its an Integer instead of
     * an int, so the value can be null.
     */
    @Getter
    @Setter
    private Integer limit;

    /**
     * Options for the event;
     */
    @Getter
    @Setter
    private EventOptions options;

    /**
     * Default constructor
     */
    public Event() {
        this.key = UUID.randomUUID().toString();
        this.products = new HashSet<>();
        this.options = new EventOptions();
    }

    /**
     * Constructor
     *
     * @param title       Title of the Event
     * @param description Description of the Event
     * @param location    Location of the Event
     * @param target      Target of the Event
     * @param limit       Limit of the Event
     * @param imageURL    Path to the Image of the Event
     * @param start       Starting DateTime of the Event
     * @param end         Ending DateTime of the Event
     */
    public Event(String title, String description, String location, int target, Integer limit, String imageURL,
                 LocalDateTime start, LocalDateTime end) {
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.target = target;
        this.limit = limit;
        this.start = start;
        this.end = end;
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
