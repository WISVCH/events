package ch.wisv.events.domain.model.user;

import ch.wisv.events.domain.model.AbstractModel;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
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

    /**
     * List of LdapGroups the user is part of.
     */
    @ElementCollection
    private List<LdapGroup> ldapGroup = new ArrayList<>();

    /**
     * User is a verified CH member.
     */
    private boolean verified;

    /**
     * Constructor Customer creates a new Customer instance.
     *
     * @param sub   of type String
     * @param name  of type String
     * @param email of type String
     */
    public User(String sub, String name, String email) {
        super();
        this.sub = sub;
        this.name = name;
        this.email = email;
    }
}
