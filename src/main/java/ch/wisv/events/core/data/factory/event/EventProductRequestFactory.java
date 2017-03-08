package ch.wisv.events.core.data.factory.event;

import ch.wisv.events.api.request.EventProductRequest;
import ch.wisv.events.core.model.event.Event;

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
