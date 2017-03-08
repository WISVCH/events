package ch.wisv.events.core.model.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Product Entity.
 */
@Entity
@AllArgsConstructor
@Data
public class Product {

    /**
     * ID of the product, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    public Integer id;

    /**
     * Key of the product, getter only so it can not be changed.
     */
    public String key;

    /**
     * Title of the product.
     */
    public String title;

    /**
     * Description of the product.
     */
    @Lob
    public String description;

    /**
     * Price/Cost of the product.
     */
    public float cost;

    /**
     * Products sold.
     */
    public int sold;

    /**
     * Maximum number of sold for the product. It is an Integer so it can be NULL.
     */
    public Integer maxSold;

    /**
     * Start DateTime for selling this product.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    public LocalDateTime sellStart;

    /**
     * End DateTime for selling this product.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    public LocalDateTime sellEnd;

    /**
     * Field productList
     */
    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    public List<Product> products;

    /**
     * Flag if product is linked
     */
    public boolean linked;

    /**
     * Default constructor.
     */
    public Product() {
        this.key = UUID.randomUUID().toString();
        this.products = new ArrayList<>();
    }

    /**
     * Constructor.
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
        if (this.maxSold == null) {
            return 100.d;
        }
        return Math.round((((double) this.sold / (double) this.maxSold) * 100.d) * 100.d) / 100.d;
    }

}
