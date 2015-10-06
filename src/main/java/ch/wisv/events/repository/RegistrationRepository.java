package ch.wisv.events.repository;

import ch.wisv.events.model.Registration;
import org.springframework.data.repository.CrudRepository;

/**
 * Registration repository.
 */
public interface RegistrationRepository extends CrudRepository<Registration, String> {

}
