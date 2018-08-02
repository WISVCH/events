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

    /**
     * NOT get general mailing.
     */
    private boolean generalMailing;

    /**
     * NOT get career mailing.
     */
    private boolean careerMailing;

    /**
     * NOT get education mailing.
     */
    private boolean educationMailing;

    /**
     * GET maCHazine.
     */
    private boolean maCHazine;
}
