package ch.wisv.events.domain.model.user;

import ch.wisv.events.domain.model.AbstractModel;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractModel {

    /**
     * WISVCH sub.
     */
    @Column(unique = true)
    private String sub;

    /**
     * Name of the User.
     */
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    /**
     * Email of the User.
     */
    @Column(unique = true)
    @Email(message = "Not a well-formed email address")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
}
