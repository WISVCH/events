package ch.wisv.events.event.legacy;

import ch.wisv.events.event.legacy.Registration;
import org.springframework.data.repository.CrudRepository;

/**
 * Registration repository.
 */
public interface RegistrationRepository extends CrudRepository<Registration, String> {

}
