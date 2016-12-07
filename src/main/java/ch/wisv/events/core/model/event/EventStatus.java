package ch.wisv.events.core.model.event;

import static java.util.Arrays.stream;

/**
 * Enum EventStatus
 */

public enum EventStatus {

    PUBLISHED(1, "Published"),
    CONCEPT(2, "Concept"),
    NOT_PUBLISHED(3, "Not published");

    /**
     * ID of the status
     */
    private final int id;

    /**
     * Display name of the status (for view purpose)
     */
    private final String displayName;

    /**
     * Default constructor
     *
     * @param id          ID of the status
     * @param displayName Display name of the status
     */
    EventStatus(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * Get status by status ID
     *
     * @param index Status ID
     * @return EventStatus corresponding to the ID
     */
    public static EventStatus getStatus(int index) {
        return stream(EventStatus.values()).filter(status -> status.getId() == index).findFirst().orElse(null);
    }

    /**
     * Get display name of the EventStatus
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get id of the EventStatus
     *
     * @return id
     */
    public int getId() {
        return id;
    }
}
