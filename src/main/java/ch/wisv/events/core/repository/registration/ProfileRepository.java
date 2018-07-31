package ch.wisv.events.core.repository.registration;

import ch.wisv.events.core.model.registration.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RegistrationRepository.
 */
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

}
