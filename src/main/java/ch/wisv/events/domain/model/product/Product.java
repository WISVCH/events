package ch.wisv.events.domain.model.product;

import ch.wisv.events.domain.model.AbstractModel;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Product entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractModel {

    /**
     * Title of the Product.
     */
    @NotEmpty(message = "Title cannot be empty")
    private String title;

}
