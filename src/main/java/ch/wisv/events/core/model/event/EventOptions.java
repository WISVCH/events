package ch.wisv.events.core.model.event;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * EventOption
 */
public class EventOptions implements Serializable {

    /**
     * Status of the Event
     */
    @Getter
    @Setter
    public EventStatus published;

    /**
     * Default constructor, with status not published.
     */
    public EventOptions() {
        this.published = EventStatus.NOT_PUBLISHED;
    }

}
