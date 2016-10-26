package ch.wisv.events.data.model.product;

import ch.wisv.events.data.model.Model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by svenp on 11-10-2016.
 */
@Entity
@AllArgsConstructor
public class Product implements Model {
    private final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue
    @Getter
    public Long id;

    @Getter
    @Setter
    public String title, key;

    @Lob
    @Getter
    @Setter
    public String description;

    @Getter
    @Setter
    public float cost;

    @Getter
    @Setter
    public Integer maxSold;

    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    public LocalDateTime sellStart, sellEnd;

    public Product() {
        this.key = UUID.randomUUID().toString();
    }

    public Product(String title, String description, float cost, Integer maxSold, LocalDateTime sellStart,
                   LocalDateTime sellEnd) {
        this();
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.maxSold = maxSold;
        this.sellStart = sellStart;
        this.sellEnd = sellEnd;
    }
}
