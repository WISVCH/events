package ch.wisv.events.domain.model.event;

import lombok.Getter;

/**
 * EventCategory enum.
 */
public enum EventCategory {

    CAREER("Career"), SOCIAL("Social"), EDUCATIONAL("Educational"), ASSOCIATION("Association");

    /**
     * Display name of the status.
     */
    @Getter
    private final String displayName;

    /**
     * EventCategory constructor.
     *
     * @param displayName of type String
     */
    EventCategory(String displayName) {
        this.displayName = displayName;
    }

}
