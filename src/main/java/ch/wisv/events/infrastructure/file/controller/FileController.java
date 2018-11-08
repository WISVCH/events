package ch.wisv.events.infrastructure.file.controller;

import ch.wisv.events.services.DocumentService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * FileController.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/uploads")
public class FileController {

    /**
     * DocumentService.
     */
    private final DocumentService documentService;

    /**
     * FileController constructor.
     *
     * @param documentService of type DocumentService
     */
    public FileController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Get the file of type image PNG.
     *
     * @param request of type HttpServletRequest
     *
     * @return Resource
     */
    @ResponseBody
    @GetMapping(value = "*", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public Resource getImagePng(HttpServletRequest request) {
        String path = request.getRequestURI();
        String name = path.substring(path.lastIndexOf('/') + 1);

        return documentService.loadFile(name);
    }
}
