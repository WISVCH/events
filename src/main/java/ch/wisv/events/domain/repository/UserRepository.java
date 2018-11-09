package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.user.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * UserRepository.
 */
@Repository
public interface UserRepository extends AbstractRepository<User> {

    /**
     * Check if there exists a User with a given email.
     *
     * @param email of type String
     *
     * @return boolean
     */
    Optional<User> getByEmail(String email);

    /**
     * Check if there exists a Use with a given sub.
     *
     * @param sub of type String
     *
     * @return boolean
     */
    Optional<User> getBySub(String sub);
}
