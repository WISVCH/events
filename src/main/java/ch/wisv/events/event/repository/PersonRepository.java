package ch.wisv.events.event.repository;

import ch.wisv.events.user.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Person repository.
 */
public interface PersonRepository extends CrudRepository<Person, String> {

    List<Person> findByName(String name);
}
