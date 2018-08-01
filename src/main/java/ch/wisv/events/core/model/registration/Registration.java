package ch.wisv.events.core.model.registration;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * Registration object.
 */
@Entity
@Data
public class Registration {

    /**
     * Field id of the Registration.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Field createdAt when the Registration has been created.
     */
    @NotEmpty
    private LocalDateTime createdAt;

    /**
     * Field signed of the Registration.
     */
    @NotEmpty
    private boolean signed;

    /**
     * Date of signing.
     */
    @DateTimeFormat(iso = ISO.DATE)
    @NotEmpty
    private LocalDate dateOfSigning;

    /**
     * Field profile of the Registration.
     */
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = Profile.class, fetch = FetchType.EAGER)
    @NotEmpty
    private Profile profile;

    /**
     * Field permissions of the Registration.
     */
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = Permissions.class, fetch = FetchType.EAGER)
    @NotEmpty
    private Permissions permissions;

    /**
     * Field permissions of the Registration.
     */
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = Study.class, fetch = FetchType.EAGER)
    @NotEmpty
    private Study study;

    /**
     * Constructor Registration creates a new Registration instance.
     */
    public Registration() {
        this.createdAt = LocalDateTime.now();
        this.profile = new Profile();
        this.study = new Study();
        this.permissions = new Permissions();
    }
}
