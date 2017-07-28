package ch.wisv.events.api.request;

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
    Integer eventID;

    /**
     * ID of a Product
     */
    Integer productID;

}
