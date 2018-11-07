package ch.wisv.events.domain.repository;

import ch.wisv.events.domain.model.AbstractModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * EventRepository.
 */
@NoRepositoryBean
public interface AbstractRepository<T extends AbstractModel> extends JpaRepository<T, Integer> {

    /**
     * Find an Object by its public reference.
     *
     * @param publicReference of type String
     *
     * @return Optional
     */
    Optional<T> findByPublicReference(String publicReference);

}
