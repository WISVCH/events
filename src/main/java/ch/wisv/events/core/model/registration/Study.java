package ch.wisv.events.core.model.registration;

import lombok.Getter;

/**
 * Study enum.
 */
public enum Study {

    BS_AM("Bachelor Applied Mathematics"),

    BS_CSE("Bachelor Computer Science and Engineering"),

    BS_DOUBLE("Double Bachelor (AP /AM)"),

    MS_AM("Master Applied Mathematics"),

    MS_CE("Master Computer Engineering"),

    MS_CS("Master Computer Science"),

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