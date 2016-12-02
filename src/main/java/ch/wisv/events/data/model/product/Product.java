package ch.wisv.events.data.model.product;


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
 * Product Entity.
 */
@Entity
@AllArgsConstructor
public class Product {

    private final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * ID of the product, getter only so it can not be changed
     */
    @Id
    @GeneratedValue
    @Getter
    public Long id;

    /**
     * Key of the product, getter only so it can not be changed
     */
    @Getter
    public String key;

    /**
     * Title of the product
     */
    @Getter
    @Setter
    public String title;

    /**
     * Description of the product
     */
    @Lob
    @Getter
    @Setter
    public String description;

    /**
     * Price/Cost of the product
     */
    @Getter
    @Setter
    public float cost;

    /**
     * Products sold
     */
    @Getter
    @Setter
    public int sold;

    /**
     * Maximum number of sold for the product. It is an
     * Integer so it can be NULL.
     */
    @Getter
    @Setter
    public Integer maxSold;

    /**
     * Start en End selling DateTime for the product.
     */
    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    public LocalDateTime sellStart, sellEnd;

    /**
     * Default constructor
     */
    public Product() {
        this.key = UUID.randomUUID().toString();
    }

    /**
     * Constructor
     *
     * @param title       Title of the product
     * @param description Description of the product
     * @param cost        Price/Cost of the product
     * @param maxSold     Maximum number sold of the product
     * @param sellStart   Start selling date
     * @param sellEnd     End selling date
     */
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

    /**
     * Calculate the progress of the products sold and the target of the event and round number to
     * two decimals.
     *
     * @return progress of event
     */
    public double calcProgress() {
        return Math.round((((double) this.sold / (double) this.maxSold) * 100.d) * 100.d) / 100.d;
    }

}
