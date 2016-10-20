package ch.wisv.events.event.model;

import static java.util.Arrays.stream;

/**
 * Created by sven on 18/10/2016.
 */
public enum EventStatus {

    PUBLISHED(1, "Published"),
    CONCEPT(2, "Concept"),
    NOT_PUBLISHED(3, "Not published");

    private final int id;

    private final String displayName;

    EventStatus(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getId() {
        return id;
    }

    public static EventStatus getStatus(int index) {
        return stream(EventStatus.values()).filter(status -> status.getId() == index).findFirst().orElse(null);
    }
}
