package ch.wisv.events.core.repository.registration;

import ch.wisv.events.core.model.registration.StudyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * StudyRepository.
 */
public interface StudyRepository extends JpaRepository<StudyDetails, Integer> {

}
