package ch.wisv.events.core.repository.registration;

import ch.wisv.events.core.model.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RegistrationRepository.
 */
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

}
