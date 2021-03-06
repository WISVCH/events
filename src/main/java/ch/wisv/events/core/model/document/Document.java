package ch.wisv.events.core.model.document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

/**
 * Document entity.
 */
@Entity
@Data
public class Document {

    /**
     * Id of the element.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Full name of the file.
     */
    private String fullName;

    /**
     * File name of the file.
     */
    private String fileName;

    /**
     * Type of the file.
     */
    private String type;

    /**
     * File content.
     */
    private byte[] file;
}
