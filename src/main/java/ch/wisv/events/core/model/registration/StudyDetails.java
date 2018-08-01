package ch.wisv.events.core.model.registration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * StudyDetails data object.
 */
@Entity
@Data
public class StudyDetails {

    /**
     * Field id of the studyDetails.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * StudyDetails study.
     */
    @NotEmpty
    private Study study;

    /**
     * First studyDetails year.
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
