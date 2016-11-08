package ch.wisv.events.data.factory.event;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.event.EventOptions;
import ch.wisv.events.data.model.event.EventStatus;
import ch.wisv.events.data.request.event.EventOptionsRequest;

/**
 * EventOptionRequestFactory
 */
public class EventOptionRequestFactory {

    /**
     * Create a EventOptions Object from EventOptionsRequest Object
     *
     * @param request EventOptionsRequest
     * @return EventOptions
     */
    public static EventOptions create(EventOptionsRequest request) {
        EventOptions newOptions = new EventOptions();
        newOptions.setPublished(EventStatus.getStatus(request.getStatus()));

        return newOptions;
    }

    /**
     * Create a EventOptionsRequest Object from Event Object
     *
     * @param event Event
     * @return EventOptionsRequest
     */
    public static EventOptionsRequest create(Event event) {
        return new EventOptionsRequest(event.getKey(), event.getOptions().getPublished().getId());
    }
}
