package ch.wisv.events.user.model;

import ch.wisv.events.event.legacy.Registration;
import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Person entity.
 */
@Entity
public class Person {

    // Not an @GeneratedValue because we want to depend on id (sub value) from OIDC
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // sub (subject) value from OIDC to identify logged-on users
    @Column(unique = true, nullable = true)
    private String oidcSub;
    private String name;
    @Column(unique = true, nullable = true)
    private String email;
    private boolean emailValidated;
    private String telephone;

    @OneToMany(mappedBy = "person")
    private Set<Registration> registrations;

    protected Person() {
    }

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getOidcSub() {
        return oidcSub;
    }

    public void setOidcSub(String oidcSub) {
        this.oidcSub = oidcSub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Set<Registration> getRegistrations() {
        return registrations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id == person.id &&
                emailValidated == person.emailValidated &&
                Objects.equals(oidcSub, person.oidcSub) &&
                Objects.equals(name, person.name) &&
                Objects.equals(email, person.email) &&
                Objects.equals(telephone, person.telephone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oidcSub, name, email, emailValidated, telephone);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("telephone", telephone)
                .add("oidcSub", oidcSub)
                .add("name", name)
                .add("email", email)
                .add("emailValidated", emailValidated)
                .add("id", id)
                .toString();
    }
}
