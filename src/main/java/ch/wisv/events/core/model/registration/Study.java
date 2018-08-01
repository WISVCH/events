package ch.wisv.events.core.model.registration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Study data object.
 */
@Entity
@Data
public class Study {

    /**
     * Field id of the study.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Study name.
     */
    @NotEmpty
    private String studyName;

    /**
     * First study year.
     */
    @NotEmpty
    private int firstStudyYear;

    /**
     * Student number.
     */
    @NotEmpty
    private String studentNumber;

    /**
     * NetID.
     */
    @NotEmpty
    private String netId;

}
