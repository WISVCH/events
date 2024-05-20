package ch.wisv.events.api.controller;

import ch.wisv.events.core.exception.normal.DocumentNotFoundException;
import ch.wisv.events.core.service.document.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * DocumentRestController class.
 */
@RestController
@RequestMapping({"/api/v1/documents","/api/v1/documents/"})
public class DocumentRestController {

    /** DocumentService. */
    private final DocumentService documentService;

    /**
     * DocumentRestController.
     *
     * @param documentService of type DocumentService
     */
    public DocumentRestController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Get the document by name.
     *
     * @param name of type String
     *
     * @return of type byte[]
     */
    @ResponseBody
    @GetMapping(value = "/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImagePng(@PathVariable String name) {
        return this.getDocumentBytes(name);
    }

    /**
     * Get the document by name.
     *
     * @param name of type String
     *
     * @return of type byte[]
     */
    @ResponseBody
    @GetMapping(value = "/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageJpeg(@PathVariable String name) {
        return this.getDocumentBytes(name);
    }

    /**
     * Get the document Bytes.
     *
     * @param name of type String
     *
     * @return byte[]
     */
    private byte[] getDocumentBytes(@PathVariable String name) {
        try {
            return documentService.getDocumentByName(name).getFile();
        } catch (DocumentNotFoundException e) {
            return new byte[0];
        }
    }
}
