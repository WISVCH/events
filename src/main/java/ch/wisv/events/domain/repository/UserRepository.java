package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.user.User;
import org.springframework.stereotype.Repository;

/**
 * UserRepository.
 */
@Repository
public interface UserRepository extends AbstractRepository<User> {

}
