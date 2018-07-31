package ch.wisv.events.core.repository.registration;

import ch.wisv.events.core.model.registration.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PermissionsRepository.
 */
public interface PermissionsRepository extends JpaRepository<Permissions, Integer> {

}
