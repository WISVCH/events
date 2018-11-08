package ch.wisv.events.domain.model.product;

import ch.wisv.events.domain.model.AbstractModel;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * ProductOption entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductOption extends AbstractModel {

    /**
     * Title of the ProductOption.
     */
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    /**
     * Additional price of the ProductOption.
     */
    @Min(value = 0, message = "Additional price should be 0 or higher")
    private double additionalPrice;
}
