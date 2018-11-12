package ch.wisv.events.services;

import ch.wisv.events.domain.exception.DocumentStorageException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import static org.springframework.util.StringUtils.cleanPath;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

/**
 * DocumentService class.
 */
@Validated
@Service
@Transactional
public class ImageService {

    /**
     * Base URL to the uploaded image.
     */
    @NotNull
    @Value("${wisvch.events.upload.image.cdn}")
    private String imageCDN;

    /**
     * Path the upload file is save to.
     */
    @NotNull
    @Value("${wisvch.events.upload.path}")
    private String uploadPath;

    /**
     * DocumentRepository constructor.
     */
    @Autowired
    public ImageService() {
    }

    /**
     * Save a MultipartFile into a Document.
     *
     * @param multipartFile of type MultipartFile
     *
     * @return String
     */
    public String saveFile(MultipartFile multipartFile) {
        Path rootLocation = Paths.get(uploadPath);

        String filename = cleanPath(multipartFile.getOriginalFilename());
        filename = filename.toLowerCase().replaceAll(" ", "-");

        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            if (multipartFile.isEmpty()) {
                throw new DocumentStorageException("File is empty");
            }
            if (filename.contains("..")) {
                throw new DocumentStorageException("File name contains an illegal characters");
            }

            Files.copy(multipartFile.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DocumentStorageException(e.getMessage());
        }

        return imageCDN + filename;
    }

    /**
     * Load file by name.
     *
     * @param filename of type String
     *
     * @return Resource
     */
    public Resource loadFile(String filename) {
        Path rootLocation = Paths.get(uploadPath);

        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new DocumentStorageException("Document " + filename + " does not existed");
            }
        } catch (MalformedURLException e) {
            throw new DocumentStorageException("Invalid URL");
        }
    }
}
