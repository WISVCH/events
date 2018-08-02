package ch.wisv.events.core.model.registration;

import lombok.Getter;

/**
 * Study enum.
 */
public enum Study {

    /**
     * Bachelor Applied Mathematics.
     */
    BS_AM("Bachelor Applied Mathematics"),

    /**
     * Bachelor Computer Science and Engineering.
     */
    BS_CSE("Bachelor Computer Science and Engineering"),

    /**
     * Double Bachelor (AP /AM).
     */
    BS_DOUBLE("Double Bachelor (AP /AM)"),

    /**
     * Master Applied Mathematics.
     */
    MS_AM("Master Applied Mathematics"),

    /**
     * Master Computer Engineering.
     */
    MS_CE("Master Computer Engineering"),

    /**
     * Master Computer Science.
     */
    MS_CS("Master Computer Science"),

    /**
     * Master Embedded Systems.
     */
    MS_ES("Master Embedded Systems");

    /**
     * Full name of the study.
     */
    @Getter
    private final String fullName;

    /**
     * Study constructor.
     *
     * @param fullName of type String.
     */
    Study(String fullName) {
        this.fullName = fullName;
    }
}
