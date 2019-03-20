package ch.wisv.events.domain.model.product;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.event.Event;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Product entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"event"})
public class Product extends AbstractModel {

    /**
     * Max length in 255 characters of the description.
     */
    private static final int MAX_LENGTH_DESCRIPTION = 255;

    /**
     * Title of the Product.
     */
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    /**
     * Description of the product in 255 chars.
     */
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "Max length of the description is 255 characters")
    private String description;

    /**
     * Price of the product.
     */
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price should be 0 or higher")
    private double price;

    /**
     * Event this product is linked to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_item_id")
    private Event event;

    /**
     * Ticket limit of the product.
     */
    @Min(value = 0, message = "Limit should be bigger than zero or zero for no limit")
    private int ticketLimit;

    /**
     * Max number of tickets per user of the product.
     */
    @Min(value = 0, message = "Max number of ticket per user should be bigger than zero")
    private int maxNumberOfTicketPerUser;

    /**
     * Product sold count.
     */
    private int sold;

    /**
     * Flag if product is for CH members only.
     */
    private boolean chOnly;

    /**
     * Flag if product must contain a product option.
     */
    private boolean mandatoryProductOption;

    /**
     * Flag if the project is reservable.
     */
    private boolean reservable;

    /**
     * Product options.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ProductOption.class)
    private List<ProductOption> productOptions = new ArrayList<>();

}
