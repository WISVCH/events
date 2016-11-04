package ch.wisv.events.data.request.event;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventOptionsRequest
 */
@Data
@NoArgsConstructor
public class EventOptionsRequest {

    /**
     * Key of an Event
     */
    public String key;

    /**
     * ID of an EventStatus
     */
    public int status;

    /**
     * Default constructor
     *
     * @param key    Key of an Event
     * @param status ID of an EventStatus
     */
    public EventOptionsRequest(String key, int status) {
        this.key = key;
        this.status = status;
    }
}
