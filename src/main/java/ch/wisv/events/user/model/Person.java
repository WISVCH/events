package ch.wisv.events.user.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Person entity.
 */
@Entity
@Data
public class Person {

    // Not an @GeneratedValue because we want to depend on id (sub value) from OIDC
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // sub (subject) value from OIDC to identify logged-on users
//    @Column(unique = true, nullable = true)
    private String oidcSub;

    private String name;

//    @Column(unique = true, nullable = true)
    private String email;

    private boolean emailValidated;

    private String telephone;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
