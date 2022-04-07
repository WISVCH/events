package ch.wisv.events.core.model.customer;

import ch.wisv.events.utils.LdapGroup;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * Customer object.
 */
@Entity
@Data
public class Customer {

    /** Image location. */
    @Value("${wisvch.connect.admin.groups}")
    private String adminGroups;

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
    @Getter
    private String name;

    /**
     * Field email of the customer.
     */
    @Column(unique = true)
    private String email;

    /**
     * Field rfidToken of the customer.
     */
    private String rfidToken;

    /**
     * Field verifiedChMember of the customers.
     */
    private boolean verifiedChMember;

    /**
     * Field ldapGroups.
     */
    @ElementCollection(targetClass = LdapGroup.class)
    private List<LdapGroup> ldapGroups;

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
        this.ldapGroups = new ArrayList<>();
        this.verifiedChMember = false;
    }

    /**
     * Constructor Customer creates a new Customer instance.
     *
     * @param sub        of type String
     * @param name       of type String
     * @param email      of type String
     * @param rfidToken  of type String
     */
    public Customer(String sub, String name, String email, String rfidToken) {
        this();
        this.sub = sub;
        this.name = name;
        this.email = email;
        this.rfidToken = rfidToken;
    }

    /**
     * Check if the customer has a ldap group that is also an admin group.
     * @return boolean
     */
    public boolean isAdmin() {
        List<String> adminGroups = List.of(this.adminGroups.split(","));
        return this.ldapGroups.stream().anyMatch(ldapGroup -> adminGroups.contains(ldapGroup.getName()));
    }
}
