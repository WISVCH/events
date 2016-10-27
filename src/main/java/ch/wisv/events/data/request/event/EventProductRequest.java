package ch.wisv.events.data.request.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by sven on 16/10/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProductRequest {

    String eventKey;

    Long eventID;

    Long productID;

}
