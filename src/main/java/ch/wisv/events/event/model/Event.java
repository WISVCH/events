package ch.wisv.events.event.model;

import ch.wisv.events.view.View;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Event entity.
 */
@Entity
@AllArgsConstructor
public class Event {

    private final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue
    @Getter
    private long id;

    @JsonView(View.Event.class)
    @Getter
    private String key;

    @Getter
    @Setter
    private String title, description, location, imageURL;

    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Product> products;

    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    private LocalDateTime start, end;

    @Getter
    @Setter
    private int sold, target;

    @Getter
    @Setter
    private Integer limit;

    @Getter
    @Setter
    private EventOptions options;

    public Event() {
        this.key = UUID.randomUUID().toString();
        this.products = new HashSet<>();
        this.options = new EventOptions();
    }

    public Event(String title, String description, String location, int target, Integer limit, LocalDateTime
            end, String imageURL, LocalDateTime start) {
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

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public double calcProgress() {
        return Math.min(Math.round((((double) this.sold / (double) this.target) * 100.d) * 100.d) / 100.d, 100.d);
    }

}
