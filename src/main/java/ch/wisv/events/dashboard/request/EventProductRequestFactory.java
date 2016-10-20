package ch.wisv.events.dashboard.request;

import ch.wisv.events.event.model.Event;

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
