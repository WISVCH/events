package ch.wisv.events.domain.model.event;

import lombok.Getter;

/**
 * EventCategory enum.
 */
public enum EventCategory {

    /**
     * Career event category.
     */
    CAREER("Career"),

    /**
     * Social event category.
     */
    SOCIAL("Social"),

    /**
     * Educational event category.
     */
    EDUCATIONAL("Educational"),

    /**
     * Association event category.
     */
    ASSOCIATION("Association");

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
