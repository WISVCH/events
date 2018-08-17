package ch.wisv.events.core.model.registration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Address object.
 */
@Entity
@Data
public class Address {

    /**
     * Field id of the customer.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Address street name.
     */
    @NotEmpty
    private String streetName;

    /**
     * Address house number.
     */
    @NotEmpty
    private String houseNumber;

    /**
     * Address zip code.
     */
    @NotEmpty
    private String zipCode;

    /**
     * Address city.
     */
    @NotEmpty
    private String city;

}
