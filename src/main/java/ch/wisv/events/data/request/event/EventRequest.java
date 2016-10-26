package ch.wisv.events.data.request.event;

import ch.wisv.events.data.model.event.EventOptions;
import ch.wisv.events.data.request.Request;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by sven on 14/10/2016.
 */
@Data
@NoArgsConstructor
public class EventRequest implements Request {

    Long id;

    String title, key, description, location, eventStart, eventEnd, image;

    int target;

    Integer limit;

    EventOptions options;

    public EventRequest(Long id, String title, String description, String location, int target, Integer limit,
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
