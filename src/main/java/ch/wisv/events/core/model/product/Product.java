package ch.wisv.events.core.model.product;

import ch.wisv.events.api.request.ProductDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.util.VatRate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
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
    public Double cost;

    /**
     * VAT of the product.
     */
    @Enumerated(EnumType.STRING)
    public VatRate vatRate;

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
     * Field redirect url.
     */
    public String redirectUrl;

    /**
     * Field event.
     */

    @ManyToOne
    @JoinTable(name = "event_products", joinColumns = @JoinColumn(name = "products_id"))
    @JsonIgnore
    public Event event;

    /**
     * Parent Product. Shares the product availability.
     */
    @ManyToOne
    @JoinColumn(name = "parent_product_id")
    public Product parentProduct;

    /**
     * Field childProducts.
     */
    @OneToMany(mappedBy = "parentProduct", cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Product> childProducts;

    /**
     * Field productList.
     */
    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Product.class, fetch = FetchType.EAGER)
    @JsonIgnore
    public List<Product> products;

    /**
     * Flag if product is linked.
     */
    public boolean linked;

    /**
     * Includes registration.
     */
    private boolean chOnly;

    /**
     * This product can be reserved instead of paid directly at checkout.
     * Defaults to false.
     */
    private boolean reservable = false;

    /**
     * Product constructor.
     */
    public Product() {
        this.key = UUID.randomUUID().toString();
        this.products = new ArrayList<>();

        // Set default sold to zero.
        this.sold = 0;
        this.reserved = 0;
        this.vatRate = VatRate.VAT_FREE;
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
        this.redirectUrl = productDto.getRedirectUrl();
        this.cost = productDto.getCost();
        this.vatRate = productDto.getVatRate();
        this.maxSold = productDto.getMaxSold();
        this.maxSoldPerCustomer = productDto.getMaxSoldPerCustomer();
        this.chOnly = productDto.isChOnly();
        this.reservable = productDto.isReservable();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (productDto.getSellStart() != null) {
            this.sellStart = LocalDateTime.parse(productDto.getSellStart(), formatter);
        }
        if (productDto.getSellEnd() != null) {
            this.sellEnd = LocalDateTime.parse(productDto.getSellEnd(), formatter);
        }
    }

    /**
     * Constructor.
     *
     * @param title       Title of the product
     * @param description Description of the product
     * @param cost        Price/Cost of the product
     * @param vatRate     VAT of the product
     * @param maxSold     Maximum number sold of the product
     * @param sellStart   Start selling date
     * @param sellEnd     End selling date
     */
    public Product(
            String title, String description, Double cost, VatRate vatRate, Integer maxSold, LocalDateTime sellStart, LocalDateTime sellEnd
    ) {
        this();
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.vatRate = vatRate;
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
        if (this.getMaxSold() == null) {
            return 100.d;
        } else if (this.getTotalSold() == 0) {
            return 0.d;
        }

        return Math.round((((double) this.getTotalSold() / (double) this.getMaxSold()) * 100.d) * 100.d) / 100.d;
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

    /**
     * Is the product sold out.
     *
     * @return boolean
     */
    public boolean isSoldOut() {
        return this.getMaxSold() != null && this.getTotalSold() >= this.getMaxSold();
    }

    public Integer getMaxSold() {
        // If the product has a parent product, return the maxSold of the parent product.
        if (this.parentProduct != null) {
            return this.parentProduct.getMaxSold();
        }

        return this.maxSold;
    }

    public Integer getMaxSoldPerCustomer() {
        // If the product has a parent product, return the maxSoldPerCustomer of the parent product.
        if (this.parentProduct != null) {
            return this.parentProduct.getMaxSoldPerCustomer();
        }

        return this.maxSoldPerCustomer;
    }

    public Integer getTotalSold() {
        // If the product has a parent product, return the total sold of the parent product.
        if (this.parentProduct != null) {
            return this.parentProduct.getTotalSold();
        }

        int totalSold = this.sold;
        if (this.childProducts != null) {
            for (Product childProduct : this.childProducts) {
                totalSold += childProduct.getSold();
            }
        }

        return totalSold;
    }

    // Custom toString
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
