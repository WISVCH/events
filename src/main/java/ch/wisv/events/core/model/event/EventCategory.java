package ch.wisv.events.core.model.event;

import lombok.Getter;

/**
 * Enum EventStatus
 */
public enum EventCategory {

    CAREER("Career"),
    SOCIAL("Social"),
    EDUCATIONAL("Educational"),
    ASSOCIATION("Association");

    /**
     * Display name of the status (for view purpose)
     */
    @Getter
    private final String displayName;

    /**
     * Default constructor
     *
     * @param displayName Display name of the status
     */
    EventCategory(String displayName) {
        this.displayName = displayName;
    }
}
