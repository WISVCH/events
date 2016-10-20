package ch.wisv.events.dashboard.request;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.EventOptions;
import ch.wisv.events.event.model.EventStatus;

/**
 * Created by sven on 19/10/2016.
 */
public class EventOptionRequestFactory {

    public static EventOptions create(EventOptionsRequest request) {
        EventOptions options = new EventOptions();
        options.setPublished(
                EventStatus.getStatus(request.getStatus())
                            );

        return options;
    }

    public static EventOptionsRequest create(Event event, EventOptions options) {
        return new EventOptionsRequest(event.getKey(), options.getPublished().getId());
    }
}
