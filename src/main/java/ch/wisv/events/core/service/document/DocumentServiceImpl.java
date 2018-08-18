package ch.wisv.events.core.service.document;

import ch.wisv.events.core.exception.normal.DocumentNotFoundException;
import ch.wisv.events.core.model.document.Document;
import ch.wisv.events.core.repository.document.DocumentRepository;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * DocumentService class.
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    /** DocumentRepository. */
    private final DocumentRepository documentRepository;

    /**
     * DocumentService constructor.
     *
     * @param documentRepository of type DocumentRepository.
     */
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Store a document.
     *
     * @param multipartFile of type MultipartFile
     *
     * @return Document
     */
    @Override
    public Document storeDocument(MultipartFile multipartFile) throws IOException {
        Document document = new Document();
        document.setFile(multipartFile.getBytes());
        document.setFullName(multipartFile.getOriginalFilename());
        document.setFileName(multipartFile.getOriginalFilename().split("\\.")[0]);
        document.setType(multipartFile.getContentType());

        return this.storeDocument(document);
    }

    /**
     * Store a document.
     *
     * @param document of type Document
     *
     * @return Document
     */
    @Override
    public Document storeDocument(Document document) {
        return documentRepository.saveAndFlush(document);
    }

    /**
     * Get document by name.
     *
     * @param image of type String
     *
     * @return byte[]
     */
    @Override
    public Document getDocumentByName(String image) throws DocumentNotFoundException {
        return documentRepository.findByFileName(image).orElseThrow(() -> new DocumentNotFoundException("name " + image));
    }
}
