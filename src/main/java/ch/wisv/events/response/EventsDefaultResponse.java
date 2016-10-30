package ch.wisv.events.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Lob;
import java.time.LocalDateTime;

/**
 * EventsDefaultResponse.
 */
@AllArgsConstructor
public class EventsDefaultResponse {

    /**
     * Key of an Event.
     */
    @Getter
    @Setter
    private String key;

    /**
     * Title of an Event
     */
    @Getter
    @Setter
    private String title;

    /**
     * Description of an Event
     */
    @Lob
    @Getter
    @Setter
    private String description;

    /**
     * Location of an Event
     */
    @Getter
    @Setter
    private String location;

    /**
     * ImageURL of an Event
     */
    @Getter
    @Setter
    private String imageURL;

    /**
     * Start of an Event
     */
    @Getter
    @Setter
    private LocalDateTime startEvent;

    /**
     * End of an Event
     */
    @Getter
    @Setter
    private LocalDateTime endEvent;

}
