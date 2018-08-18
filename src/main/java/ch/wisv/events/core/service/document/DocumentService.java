package ch.wisv.events.core.service.document;

import ch.wisv.events.core.exception.normal.DocumentNotFoundException;
import ch.wisv.events.core.model.document.Document;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * DocumentService interface.
 */
public interface DocumentService {

    /**
     * Store a document.
     *
     * @param multipartFile of type MultipartFile
     *
     * @return Document
     */
    Document storeDocument(MultipartFile multipartFile) throws IOException;

    /**
     * Store a document.
     *
     * @param document of type Document
     *
     * @return Document
     */
    Document storeDocument(Document document);

    /**
     * Get document by name.
     *
     * @param image of type String
     *
     * @return Document
     *
     * @throws DocumentNotFoundException when Document is not found
     */
    Document getDocumentByName(String image) throws DocumentNotFoundException;
}
