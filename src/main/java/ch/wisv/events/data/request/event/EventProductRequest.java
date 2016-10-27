package ch.wisv.events.data.request.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventProductRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProductRequest {

    /**
     * Key of an Event
     */
    String eventKey;

    /**
     * ID of an Event
     */
    Long eventID;

    /**
     * ID of a Product
     */
    Long productID;

}
