package ch.wisv.events.core.model.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
public class Customer {

    /**
     * Field id of the customer.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Field key UUID of the customer.
     */
    @Column(unique = true)
    private String key;

    /**
     * Field sub is the OIDC auth unique ID.
     */
    @Column(unique = true)
    private String sub;

    /**
     * Field name of the customer.
     */
    private String name;

    /**
     * Field email of the customer.
     */
    @Column(unique = true)
    private String email;

    /**
     * Field chUsername of the customer, this will be the ldap username.
     */
    @Column(unique = true)
    private String chUsername;

    /**
     * Field rfidToken of the customers pass.
     */
    private String rfidToken;

    /**
     * Field ldapGroups.
     */
    @ElementCollection(targetClass = ch.wisv.events.utils.LdapGroup.class)
    private List<ch.wisv.events.utils.LdapGroup> ldapGroups;

    /**
     * Field createdAt when the Customer has been created.
     */
    private LocalDateTime createdAt;

    /**
     * Constructor Customer creates a new Customer instance.
     */
    public Customer() {
        this.key = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor Customer creates a new Customer instance.
     *
     * @param sub        of type String
     * @param name       of type String
     * @param email      of type String
     * @param chUsername of type String
     * @param rfidToken  of type String
     */
    public Customer(String sub, String name, String email, String chUsername, String rfidToken) {
        this();
        this.sub = sub;
        this.name = name;
        this.email = email;
        this.chUsername = chUsername;
        this.rfidToken = rfidToken;
    }
}
