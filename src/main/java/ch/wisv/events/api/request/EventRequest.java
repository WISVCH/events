package ch.wisv.events.api.request;

import ch.wisv.events.core.model.event.EventOptions;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventRequest
 */
@Data
@NoArgsConstructor
public class EventRequest {

    /**
     * ID of an Event
     */
    Integer id;

    /**
     * Title, Key, Description, Location, Event Start, Event End, and Image Path of an Event
     */
    String title, key, description, location, eventStart, eventEnd, image;

    /**
     * Target of an Event
     */
    int target;

    /**
     * Limit of an Event
     */
    Integer limit;

    /**
     * EventOptions of an Event
     */
    EventOptions options;

    /**
     * Default Constructor
     *
     * @param id          ID of an Event
     * @param title       Title of an Event
     * @param description Description of an Event
     * @param location    Location of an Event
     * @param target      Target of an Event
     * @param limit       Limit of an Event
     * @param eventStart  Start of an Event
     * @param eventEnd    End of an Event
     * @param image       Image path of an Event
     * @param key         Key of an Event
     * @param options     EventOptions of an Event
     */
    public EventRequest(Integer id, String title, String description, String location, int target, Integer limit,
                        String eventStart, String eventEnd, String image, String key, EventOptions options) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.target = target;
        this.limit = limit;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.image = image;
        this.key = key;
        this.options = options;
    }

}
