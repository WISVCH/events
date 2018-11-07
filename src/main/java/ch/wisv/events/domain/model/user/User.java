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

    @Column(unique = true)
    private String sub;

    @NotEmpty
    private String name;

    @Column(unique = true)
    @Email
    @NotEmpty
    private String email;

    public User() {
        super();
    }
}
