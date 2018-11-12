package ch.wisv.events.services;

import ch.wisv.events.domain.exception.DocumentStorageException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * DocumentService class.
 */
@Validated
@Service
public class LogService {

    /**
     * Base URL to the uploaded image.
     */
    @NotNull
    @Value("${logging.file}")
    private String loggingFile;

    /**
     * Load file by name.
     *
     * @return Resource
     */
    public String loadLoggingFile() {
        try {
            File logFile = new File(loggingFile);

            return Files.toString(logFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new DocumentStorageException(e.getMessage());
        }
    }
}
