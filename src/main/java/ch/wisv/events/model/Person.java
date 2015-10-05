package ch.wisv.events.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Objects;
import java.util.Set;

/**
 * Person entity.
 */
@Entity
public class Person {

    // Not an @GeneratedValue because we want to depend on id (sub value) from OIDC
    @Id
    long id;
    private String name;
    private String email;
    private String telephone;

    @OneToMany(mappedBy = "person")
    private Set<Registration> registrations;

    protected Person() {
    }

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
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
        return Objects.equals(id, person.id) &&
                Objects.equals(name, person.name) &&
                Objects.equals(email, person.email) &&
                Objects.equals(telephone, person.telephone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, telephone);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("email", email)
                .add("telephone", telephone)
                .toString();
    }
}
