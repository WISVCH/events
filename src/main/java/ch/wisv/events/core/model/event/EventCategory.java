package ch.wisv.events.core.model.event;

/**
 * Enum EventStatus
 */
public enum EventCategory {

    CAREER("Career"),
    SOCIAL("Social"),
    EDUCATIONAL("Educational");

    /**
     * Display name of the status (for view purpose)
     */
    private final String displayName;

    /**
     * Default constructor
     *
     * @param displayName Display name of the status
     */
    EventCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get display name of the EventStatus
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
