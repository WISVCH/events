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
public class Address {

    /**
     * Field id of the customer.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    private String streetName;

    private String houseNumber;

    private String zipCode;

    private String city;

}