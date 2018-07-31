package ch.wisv.events.core.model.registration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Address class.
 */
@Entity
@Data
public class Permissions {

    /**
     * Field id of the customer.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    private boolean generalMailing;

    private boolean careerMailing;

    private boolean educationMailing;

    private boolean maCHazine;
}