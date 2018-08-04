package ch.wisv.events.core.model.product;

import ch.wisv.events.api.request.ProductDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * Product Entity.
 */
@Entity
@Data
public class Product {

    /**
     * ID of the product, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    public Integer id;

    /**
     * Key of the product, getter only so it can not be changed.
     */
    @Column(unique = true)
    public String key;

    /**
     * Title of the product.
     */
    public String title;

    /**
     * Description of the product.
     */
    @Column(columnDefinition = "TEXT")
    public String description;

    /**
     * Price/Cost of the product.
     */
    @NotEmpty
    public Double cost;

    /**
     * Products sold.
     */
    public int sold;

    /**
     * Products sold.
     */
    public int reserved;

    /**
     * Maximum number of sold for the product. It is an Integer so it can be NULL.
     */
    public Integer maxSold;

    /**
     * Field maxSoldPerCustomer.
     */
    public Integer maxSoldPerCustomer;

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
     * Field productList.
     */
    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    public List<Product> products;

    /**
     * Flag if product is linked.
     */
    public boolean linked;

    /**
     * Includes registration.
     */
    private boolean includesRegistration;

    /**
     * Includes registration.
     */
    private boolean chOnly;

    /**
     * Product constructor.
     */
    public Product() {
        this.key = UUID.randomUUID().toString();
        this.products = new ArrayList<>();

        // Set default sold to zero.
        this.sold = 0;
        this.reserved = 0;
    }

    /**
     * ProductDto constructor.
     *
     * @param productDto of type ProductDto
     */
    public Product(ProductDto productDto) {
        this();
        this.title = productDto.getTitle();
        this.description = productDto.getDescription();
        this.cost = productDto.getCost();
        this.maxSold = productDto.getMaxSold();
        this.maxSoldPerCustomer = productDto.getMaxSoldPerCustomer();
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
    public Product(
            String title, String description, Double cost, Integer maxSold, LocalDateTime sellStart, LocalDateTime sellEnd
    ) {
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
        } else if (this.sold == 0) {
            return 0.d;
        }

        return Math.round((((double) this.sold / (double) this.maxSold) * 100.d) * 100.d) / 100.d;
    }

    /**
     * Increase sold count by amount.
     *
     * @param amount of type Integer
     */
    public void increaseSold(int amount) {
        this.sold += amount;
    }

    /**
     * Increase sold count by amount.
     *
     * @param amount of type Integer
     */
    public void increaseReserved(int amount) {
        this.reserved += amount;
    }
}
