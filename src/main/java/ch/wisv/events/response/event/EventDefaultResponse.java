package ch.wisv.events.response.event;

import ch.wisv.events.data.model.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Lob;
import java.time.LocalDateTime;

/**
 * EventDefaultResponse.
 */
@AllArgsConstructor
public class EventDefaultResponse {

    /**
     * Key of an Event.
     */
    @Getter
    private String key;

    /**
     * Title of an Event
     */
    @Getter
    private String title;

    /**
     * Description of an Event
     */
    @Lob
    @Getter
    private String description;

    /**
     * Location of an Event
     */
    @Getter
    private String location;

    /**
     * ImageURL of an Event
     */
    @Getter
    private String imageURL;

    /**
     * Start of an Event
     */
    @Getter
    private LocalDateTime startEvent;

    /**
     * End of an Event
     */
    @Getter
    private LocalDateTime endEvent;

    /**
     * Constructor by Event
     *
     * @param event Event
     */
    public EventDefaultResponse(Event event) {
        this.key = event.getKey();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.imageURL = event.getImageURL();
        this.startEvent = event.getStart();
        this.endEvent = event.getEnd();
    }

}
