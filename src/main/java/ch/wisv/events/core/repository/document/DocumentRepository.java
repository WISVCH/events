package ch.wisv.events.core.repository.document;

import ch.wisv.events.core.model.document.Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DocumentRepository interface.
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find a document by fileName.
     *
     * @param fileName of type String
     *
     * @return Optional
     */
    Optional<Document> findByFileName(String fileName);
}
