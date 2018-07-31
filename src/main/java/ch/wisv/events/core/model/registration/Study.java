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

    @NotEmpty
    private String studyName;

    @NotEmpty
    private int firstStudyYear;

    @NotEmpty
    private String studentNumber;

    @NotEmpty
    private String netId;

}
