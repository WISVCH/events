package ch.wisv.events.domain.model.event;

import lombok.Getter;

/**
 * EventStatus enum.
 */
public enum EventStatus {

    /**
     * Event is published.
     */
    PUBLISHED("Published"),

    /**
     * Event is not published.
     */
    NOT_PUBLISHED("Not published");

    /**
     * Display name of the status
     */
    @Getter
    private final String displayName;

    /**
     * EventStatus constructor.
     *
     * @param displayName of type String
     */
    EventStatus(String displayName) {
        this.displayName = displayName;
    }
}
