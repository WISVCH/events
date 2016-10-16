package ch.wisv.events.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by sven on 14/10/2016.
 */
@Data
@Accessors(fluent = true)
@NoArgsConstructor
public class EventRequest {

    String title;

    String key;

    String description;

    String location;

    String eventStart;

    String eventEnd;

    int limit;

    String image;
}
