package ch.wisv.events.data.factory.event;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventProductRequest;

/**
 * Created by sven on 19/10/2016.
 */
public class EventProductRequestFactory {

    public static EventProductRequest create(Event event) {
        return new EventProductRequest(
                event.getKey(),
                event.getId(),
                null
        );
    }
}
