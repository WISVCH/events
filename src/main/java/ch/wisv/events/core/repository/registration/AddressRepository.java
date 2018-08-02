package ch.wisv.events.core.repository.registration;

import ch.wisv.events.core.model.registration.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RegistrationRepository.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {

}
