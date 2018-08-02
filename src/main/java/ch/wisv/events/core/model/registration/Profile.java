package ch.wisv.events.core.model.registration;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.format.annotation.DateTimeFormat.*;

/**
 * Profile extension.
 */
@Entity
@Data
public class Profile {

    /**
     * Profile id.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Member Initials.
     */
    @NotEmpty
    private String initials;

    /**
     * Member first name.
     */
    @NotEmpty
    private String firstName;

    /**
     * Member surname prefix.
     */
    private String surnamePrefix;

    /**
     * Member surname.
     */
    @NotEmpty
    private String surname;

    /**
     * Member gender.
     */
    @NotEmpty
    private Gender gender;

    /**
     * Member date of birth.
     */
    @DateTimeFormat(iso = ISO.DATE)
    @NotEmpty
    private LocalDate dateOfBirth;

    /**
     * Member address.
     */
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = Address.class, fetch = FetchType.EAGER)
    @NotEmpty
    private Address address;

    /**
     * Member email.
     */
    @NotEmpty
    private String email;

    /**
     * Member phone number.
     */
    @NotEmpty
    private String phoneNumber;

    /**
     * Member ice contact name.
     */
    @NotEmpty
    private String iceContactName;

    /**
     * Member ice contact phone number.
     */
    @NotEmpty
    private String iceContactPhone;

    /**
     * Constructor for Profile.
     */
    public Profile() {
        this.address = new Address();
    }
}
