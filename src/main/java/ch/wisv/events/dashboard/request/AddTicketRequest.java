package ch.wisv.events.dashboard.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by sven on 16/10/2016.
 */
@Data
@NoArgsConstructor
public class AddTicketRequest {

    String eventKey;

    String ticketKey;

}
