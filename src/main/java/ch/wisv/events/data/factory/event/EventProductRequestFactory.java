package ch.wisv.events.data.factory.event;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventProductRequest;

/**
 * EventProductRequestFactory
 */
public class EventProductRequestFactory {

    /**
     * Create EventProductRequest from Event
     *
     * @param event Event
     * @return EventProductRequest
     */
    public static EventProductRequest create(Event event) {
        return new EventProductRequest(
                event.getKey(),
                event.getId(),
                null
        );
    }
}
